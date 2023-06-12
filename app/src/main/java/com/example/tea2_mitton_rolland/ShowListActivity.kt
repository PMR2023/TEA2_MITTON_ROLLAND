package com.example.tea1_v01

import android.annotation.SuppressLint
import android.content.ClipData.Item
import android.content.Context
import android.content.Intent
import android.graphics.Paint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import org.json.JSONObject

class ShowListActivity : AppCompatActivity(), ItemAdapter.OnItemCheckListener {

    private lateinit var nomPseudoActif: TextView
    private lateinit var ListdeProfilsDeListeToDo: MutableList<ProfilListeToDo>

    private lateinit var toolbar: Toolbar

    private lateinit var editTextNewList: EditText
    private lateinit var btnOk: Button

    private lateinit var adapter: ItemAdapter
    private var dropdownItems: List<ItemToDo> = emptyList()

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_show_list)

        // Initialisation des vues
        toolbar = findViewById(R.id.toolbarParametres)
        setSupportActionBar(toolbar)
        nomPseudoActif = findViewById(R.id.nomProfilActif)
        editTextNewList = findViewById(R.id.editTextNewList)
        btnOk = findViewById(R.id.btnOk)

        // Récupération des données passées par l'activité précédente
        val selectedItem = intent.getIntExtra("selectedItem", -1)
        val pseudoActif = intent.getStringExtra("pseudoActif")

        var hash = getHashFromSharedPref()
        var listId = intent.getStringExtra("listId")
        Log.i("PMR",listId!!)

        apiCallGetItems(hash,listId!!)
        // Récupération du profil actif et de la liste sélectionnée
        /*val profilActif = findProfilListeToDoByLogin(getProfilListeToDoList(), pseudoActif!!)!!
        val todoListActive: ListeToDo = profilActif.getMesListesToDo().get(selectedItem)

        // Affichage du pseudo actif et du titre de la liste
        nomPseudoActif.text = "Profil : $pseudoActif / TodoList : " + todoListActive.getTitreListeToDo()

        // Configuration du RecyclerView et de l'adaptateur
        val recyclerView = findViewById<RecyclerView>(R.id.recyclerViewShowActivity)
        recyclerView.layoutManager = LinearLayoutManager(this)
        dropdownItems = todoListActive.getLesItems().toList()
        adapter = ItemAdapter(dropdownItems, this)
        recyclerView.adapter = adapter*/

        btnOk.setOnClickListener {
            // Actions à effectuer lorsque le bouton OK est cliqué
            val newTodo: String = editTextNewList.text.toString()
            editTextNewList.setText("") // Vide le contenu du champ EditText


            if (newTodo != "") {
                apiCallSetItem(hash,newTodo,listId)
                /*
                ListdeProfilsDeListeToDo = getProfilListeToDoList()
                findProfilListeToDoByLogin(ListdeProfilsDeListeToDo, pseudoActif)!!
                val todoListString = getToDoListAsString(todoListActive)

                if (todoListString.contains(newTodo)) {
                    Toast.makeText(applicationContext, "Cette ToDo existe déjà", Toast.LENGTH_SHORT).show()
                } else {
                    // Ajouter la nouvelle ToDo à la liste active
                    todoListActive.addItem(newTodo)
                    dropdownItems = todoListActive.getLesItems().toList()
                    adapter.updateDataWithoutNotify(dropdownItems)
                    adapter.notifyDataSetChanged()

                    updateListeToDoInProfilListeToDoList(ListdeProfilsDeListeToDo, todoListActive)

                    saveProfilListeToDoList(ListdeProfilsDeListeToDo)
                }

                // Mettre à jour la liste affichée dans le RecyclerView
                val dropdownItems = todoListActive.getLesItems().toList()
                adapter.updateDataWithoutNotify(dropdownItems)
            }*/
            }
        }
    }

    private fun apiCallSetItem(hash:String,newTodo: String,listId: String) {

        val userId = intent.getStringExtra("userId")
        val pseudo = intent.getStringExtra("pseudoActif")!!
        val listId = intent.getStringExtra("listId")

        Log.i("Volley","Création d'un item pour la liste " + listId + intent.getStringExtra("pseudoActif"))
        val url = "http://tomnab.fr/todo-api/lists/$listId/items?label=$newTodo"
        val headers = HashMap<String, String>()
        headers["hash"] = hash

        val request = object : StringRequest(Method.POST, url,
            Response.Listener { response ->
                apiCallGetItems(hash,listId!!)
            },
            Response.ErrorListener { error ->
                println(error.toString())
            }
        ) {
            override fun getHeaders(): MutableMap<String, String> {
                return headers
            }
        }

        val queue = Volley.newRequestQueue(this)
        queue.add(request)
    }

    private fun apiCallGetItems(hash: String,listId:String) {
        //val url = "http://tomnab.fr/todo-api/lists/$listId/items"
        //val request = JsonObjectRequest(
            Log.i("Volley", "Appel des Users ...")

            val url = "http://tomnab.fr/todo-api/lists/$listId/items"
        val headers = HashMap<String, String>()
        headers["hash"] = getHashFromSharedPref()

        val request = object : StringRequest(
            Method.GET, url,
            Response.Listener<String> { response ->
                // Convertir la chaîne de caractères JSON en un objet JSON
                val jsonResponse = JSONObject(response)
                // Récupérer le hash du JSON
                val items = jsonResponse["items"].toString()
                Log.i("Volley", "Appel des users réussie : $items")

                val gson = Gson()
                val listType = object : TypeToken<List<Map<String, String>>>() {}.type
                val list: List<Map<String, String>> = gson.fromJson(items, listType)
                val listItemToDo = mutableListOf<ItemToDo>()
                for(el in list){
                    var fait = false
                    if(el["checked"]=="1") fait = true
                    listItemToDo.add(ItemToDo(el["label"].toString(),fait))
                }
                val itemAdapter = ItemAdapter(listItemToDo,this)
                val recyclerView = findViewById<RecyclerView>(R.id.recyclerViewShowActivity)
                recyclerView.layoutManager = LinearLayoutManager(this)
                recyclerView.adapter = itemAdapter
            },
            Response.ErrorListener { error ->
                Log.i("Volley", error.toString())
            }) {
            override fun getHeaders(): MutableMap<String, String> {
                return headers
            }
        }

        val queue = Volley.newRequestQueue(this)
        queue.add(request)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_settings -> {
                // Actions à effectuer lorsque le bouton des paramètres est cliqué
                finish()
                val intent = Intent(this, SettingsActivity::class.java)
                startActivity(intent)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    // Enregistrer une liste de ProfilListeToDo dans les préférences partagées
    fun saveProfilListeToDoList(profilListeToDoList: MutableList<ProfilListeToDo>) {
        val sharedPreferences = getSharedPreferences("MyAppPreferences", Context.MODE_PRIVATE)
        val gson = Gson()
        val json = gson.toJson(profilListeToDoList)
        val editor = sharedPreferences.edit()
        editor.putString("profilListeToDoList", json)
        editor.apply()
    }

    // Fonction qui prend en paramètre une liste de ProfilListeToDo ainsi qu'un login, et renvoie la ProfilListeToDo qui correspond au login
    fun findProfilListeToDoByLogin(profilListeToDoList: List<ProfilListeToDo>, login: String): ProfilListeToDo? {
        return profilListeToDoList.find { it.login == login }
    }

    // Récupérer une liste de ProfilListeToDo depuis les préférences partagées
    fun getProfilListeToDoList(): MutableList<ProfilListeToDo> {
        val sharedPreferences = getSharedPreferences("MyAppPreferences", Context.MODE_PRIVATE)
        val gson = Gson()
        val json = sharedPreferences.getString("profilListeToDoList", "")
        val type = object : TypeToken<MutableList<ProfilListeToDo>>() {}.type
        return gson.fromJson(json, type) ?: mutableListOf()
    }

    /*    // Convertir la liste de ToDo en liste d'objets DropdownItem2
    fun fromListToDoToDropDownItem2(listeToDo: ListeToDo, pseudoActif: String): List<DropdownItem2> {
        val dropdownItems = mutableListOf<DropdownItem2>()
        for ((index, item) in listeToDo.getLesItems().withIndex()) {
            dropdownItems.add(DropdownItem2(index + 1, item.getDescription(), pseudoActif))
        }
        return dropdownItems
    }*/

    // Convertir la liste de ToDo en liste de chaînes de caractères
    fun getToDoListAsString(listeToDo: ListeToDo): MutableList<String> {
        val listeDeToDoString: MutableList<String> = mutableListOf()
        for (item: ItemToDo in listeToDo.getLesItems()) {
            listeDeToDoString.add(item.getDescription())
        }
        return listeDeToDoString
    }

    // Mettre à jour une liste de ToDo dans la liste de profils
    fun updateListeToDoInProfilListeToDoList(profilListeToDoList: MutableList<ProfilListeToDo>, listeToDo: ListeToDo) {
        val listeToDoName = listeToDo.getTitreListeToDo()
        for (profilListeToDo in profilListeToDoList) {
            val mesListesToDo = profilListeToDo.getMesListesToDo()
            for (i in 0 until mesListesToDo.size) {
                val currentListeToDo = mesListesToDo[i]
                if (currentListeToDo.getTitreListeToDo() == listeToDoName) {
                    mesListesToDo[i] = listeToDo
                    break
                }
            }
        }
    }

    fun updateItemToDo(item: ItemToDo, todoListActive: ListeToDo): Int {
        val descriptionToUpdate = item.getDescription()
        val listOfItem = todoListActive.getLesItems()
        for (i in 0 until listOfItem.size) {
            val currentItem = listOfItem[i]
            if (currentItem.getDescription() == descriptionToUpdate) {
                // Mettre à jour les attributs de l'ItemToDo existant avec les attributs du nouvel ItemToDo
                currentItem.setFait(item.getFait())
                // Sortir de la boucle une fois la mise à jour effectuée
                return i
            }
            todoListActive.setLesItems(listOfItem)
        }
        return -1 // Retourner -1 si l'élément n'a pas été trouvé
    }




    override fun onItemChecked(item: ItemToDo, isChecked: Boolean) {

        val pseudoActif = intent.getStringExtra("pseudoActif")
        val selectedListe = intent.getIntExtra("selectedItem", -1) //Numéro de la ListeToDo séléctionnée dans ChoixListeActivity

          ListdeProfilsDeListeToDo = getProfilListeToDoList()

        /*val profilActif = findProfilListeToDoByLogin(ListdeProfilsDeListeToDo, pseudoActif!!)!!
        val todoListActive = profilActif.getMesListesToDo().get(selectedListe)

        profilActif.getMesListesToDo()
        val liste = todoListActive.getLesItems()

        if (liste.isNotEmpty()) {
            item.setFait(isChecked)

            val updatedIndex = updateItemToDo(item, todoListActive)
            if (updatedIndex != -1) {
                dropdownItems = todoListActive.getLesItems().toList()
                adapter.updateDataWithoutNotify(dropdownItems)
                //adapter.notifyItemChanged(updatedIndex)
            }
            updateListeToDoInProfilListeToDoList(ListdeProfilsDeListeToDo, todoListActive)

            //adapter.notifyDataSetChanged()


            saveProfilListeToDoList(ListdeProfilsDeListeToDo)
            Log.i("PMR","Contenu de item : $item")

            Log.i("PMR","Contenu de todoListActive : $todoListActive")
            Log.i("PMR","Contenu de selectedListe : $selectedListe")
            Log.i("PMR","Contenu de ListdeProfilsDeListeToDo : $ListdeProfilsDeListeToDo")

        } else {
            // Gérer le cas où le tableau est vide ou selectedItem est invalide

        }*/
    }
    fun getHashFromSharedPref(): String {
        val sharedPreferences = getSharedPreferences("MyAppPreferences", Context.MODE_PRIVATE)
        if (sharedPreferences.contains("hashCode")){
            val gson = Gson()
            val json = sharedPreferences.getString("hashCode", "")
            val type = object : TypeToken<String>() {}.type
            return (gson.fromJson(json, type) )
        }
        else return ""
    }
    fun saveListsToSharedPref(lists:String) {
        Log.i("Volley","[saveListsToSharedPref] enregistrement des listes dans les prefs partagées...")
        val sharedPreferences = getSharedPreferences("MyAppPreferences", Context.MODE_PRIVATE)
        val gson = Gson()
        Log.i("Volley","[saveListsToSharedPref] Contenu de lists : $lists")
        val json = gson.toJson(lists)
        val editor = sharedPreferences.edit()
        editor.putString("lists", json)
        editor.apply()
    }
}
