package com.example.tea1_v01

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
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

class ChoixListActivity : BaseActivity() {

    private lateinit var nomPseudoActif: TextView
    private lateinit var toolbar: Toolbar
    lateinit var profilListeToDo2: ProfilListeToDo


    private lateinit var editTextNewList: EditText
    private lateinit var buttonOk: Button

    private var pseudoList2: ArrayList<String> = ArrayList()
    private var todoListList: ArrayList<String> = ArrayList()
    private lateinit var ListdeProfilsDeListeToDo: MutableList<ProfilListeToDo>

    private lateinit var profilActif: ProfilListeToDo


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_choix_list)

        toolbar = findViewById(R.id.toolbarParametres)
        setSupportActionBar(toolbar)

        editTextNewList = findViewById(R.id.editTextNewList)
        buttonOk = findViewById(R.id.btnOk)

        val pseudoActif = intent.getStringExtra("pseudoActif")
        apiCallGetUser(pseudoActif!!)

        val userId = intent.getStringExtra("userId")

        Log.i("Volley", userId.toString()+pseudoActif.toString())

        nomPseudoActif = findViewById(R.id.nomProfilActif)
        nomPseudoActif.text = "Profile : $pseudoActif"


        val recyclerView = findViewById<RecyclerView>(R.id.recyclerViewChoixActivity)
        recyclerView.layoutManager = LinearLayoutManager(this)


        var hash = getHashFromSharedPref()

        //Premier appel pour avoir la liste des to-do lists
        apiCallGetLists(hash,pseudoActif!!)

        //profilListeToDo1 = ajouterListe_test(pseudoActif)
        //profilListeToDo2 = findProfilListeToDoByLogin(getProfilListeToDoList(), pseudoActif!!)!!

        //val dropdownItems = fromListToDoToDropDownItem(profilListeToDo2,pseudoActif)

        //val adapter = DropdownAdapter(dropdownItems)
        //recyclerView.adapter = adapter

        //Quand on clique sur le bouton OK:
        buttonOk.setOnClickListener {
            //actions à effectuer quand on appuye sur OK
            /*val newTodoList = editTextNewList.text.toString() //On récupère le pseudo qui est dans le champ

            if(newTodoList!="") { //on vérifie qu'il y ai bien un nom rentré

                ListdeProfilsDeListeToDo=getProfilListeToDoList()//charge la liste des pseudos à pertir des préférences partagées

                profilActif = findProfilListeToDoByLogin(ListdeProfilsDeListeToDo,pseudoActif)!!
                todoListList = getListeToDoListAsString(profilActif)

                if (todoListList.contains(newTodoList)) {//
                    Toast.makeText(applicationContext, "Cette liste existe déjà", Toast.LENGTH_SHORT).show()

                } else {
                    val newliste = ListeToDo()
                    newliste.setTitreListeToDo(newTodoList)

                    profilActif.ajouterListe(newliste)

                }

                profilListeToDo2 = profilActif

                val dropdownItems = fromListToDoToDropDownItem(profilListeToDo2,pseudoActif)
                //adapter.updateData(dropdownItems)
                //adapter.notifyDataSetChanged()


                saveProfilListeToDoList(ListdeProfilsDeListeToDo) //On sauvegarde les modifs
}
*/
            val newTodoList = editTextNewList.text.toString()
            apiCallSetList(hash, newTodoList)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)


        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_settings -> {
                // Action à effectuer lorsque le bouton des paramètres est cliqué
                finish()
                val intent = Intent(this, SettingsActivity::class.java)
                Log.i("PMR", "[OPENED]SettingsActivity")
                startActivity(intent)
                true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }

    fun fromListToDoToDropDownItem(
        profilListeToDo1: ProfilListeToDo,
        pseudoActif: String
    ): List<DropdownItem> {
        val i = 1
        val dropdownItems = mutableListOf<DropdownItem>()
        for (listei in profilListeToDo1.getMesListesToDo()) {
            dropdownItems.add(DropdownItem(i, listei.getTitreListeToDo(), pseudoActif))
        }
        return (dropdownItems)
    }

    /*fun ajouterListe_test(monLogin: String?) : ProfilListeToDo{
        // Création d'une instance de ProfilListeToDo
        val profil = ProfilListeToDo(monLogin)

        // Création d'une liste de tâches
        var liste = ListeToDo()
        liste.setTitreListeToDo("Ma liste de tâches")

        //creation des tâches

        var item1 = ItemToDo("Faire la vaisselle")
        var item2 = ItemToDo("Faire la vaisselle encore")
        var item3 = ItemToDo("reFaire la vaisselle")
        var items = arrayListOf(item1, item2, item3)
        liste.setLesItems(items)

        // Ajout de la liste à l'instance de ProfilListeToDo
        profil.ajouterListe(liste)

        liste = ListeToDo()
        liste.setTitreListeToDo("Mon autre liste de tâches")

        //creation des tâches
        item1 = ItemToDo("Faire la vaisselle")
        item2 = ItemToDo("Faire la vaisselle encore")
        item3 = ItemToDo("reFaire la vaisselle")
        items = arrayListOf(item1, item2, item3)

        liste.setLesItems(items)

        // Ajout de la liste à l'instance de ProfilListeToDo
        profil.ajouterListe(liste)

        //On marque un item comme fait
        val itemModified = profil.getMesListesToDo()[0].rechercherItem("Faire la vaisselle")

        if (itemModified != null) {
            itemModified.setFait(true)
        }

        // Vérification que la liste a été ajoutée avec succès
        println( profil.toString())

        return(profil)

    }*/


    // Enregistrer une liste de ProfilListeToDo dans les préférences partagées
    fun saveProfilListeToDoList(profilListeToDoList: MutableList<ProfilListeToDo>) {
        val sharedPreferences = getSharedPreferences("MyAppPreferences", Context.MODE_PRIVATE)
        val gson = Gson()
        val json = gson.toJson(profilListeToDoList)
        val editor = sharedPreferences.edit()
        editor.putString("profilListeToDoList", json)
        editor.apply()
    }

    //fonction qui prend en paramètre une liste de ProfilListeToDo ainsi qu'un login, et renvoie la ProfilListeToDo qui correspond au login
    fun findProfilListeToDoByLogin(
        profilListeToDoList: List<ProfilListeToDo>,
        login: String
    ): ProfilListeToDo? {
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


    //Transformer la liste de profils en liste de leurs noms
    fun getListeToDoListAsString(listesDuProfil: ProfilListeToDo): ArrayList<String> {
        val lestdestodo: ArrayList<String> = ArrayList()
        for (liste: ListeToDo in listesDuProfil.getMesListesToDo()) {
            liste.getTitreListeToDo().let { lestdestodo.add(it) }
        }
        return (lestdestodo)
    }

    //Appel de l'api pour obtenir les listes et les afficher
    fun apiCallGetLists(hash: String,pseudoActif: String) {
        val url = "http://tomnab.fr/todo-api/lists?hash=$hash"

        val request = JsonObjectRequest(
            Request.Method.GET, url, null,
            { response ->

                // Récupérer le hash du JSON
                val lists = response["lists"].toString()

                Log.i("Volley", "Api: lists : $lists")
                Toast.makeText(applicationContext, "Connexion réussie", Toast.LENGTH_SHORT).show()
                saveListsToSharedPref(lists)

                //profilListeToDo1 = ajouterListe_test(pseudoActif)
                //profilListeToDo2 = findProfilListeToDoByLogin(getProfilListeToDoList(), pseudoActif!!)!!

                val recyclerView = findViewById<RecyclerView>(R.id.recyclerViewChoixActivity)
                val layoutManager = LinearLayoutManager(this)
                recyclerView.layoutManager = layoutManager

                val gson = Gson()
                val listType = object : TypeToken<List<Map<String, String>>>() {}.type
                val list: List<Map<String, String>> = gson.fromJson(lists, listType)
                Log.i("Volley", list.toString())
                val listOfTodoLists = mutableListOf<DropdownItem>()
                for(el in list){
                    listOfTodoLists.add(DropdownItem(el["id"]!!.toInt(),el["label"].toString(),pseudoActif))
                }
                val adapter = DropdownAdapter(listOfTodoLists)

                recyclerView.adapter = adapter



            },
            { error ->
                Log.e("Volley", error.toString())
            })

        val queue = Volley.newRequestQueue(this)
        queue.add(request)
    }

    fun apiCallSetList(hash: String, newTodoList: String){
        val userId = intent.getStringExtra("userId")
        val pseudo = intent.getStringExtra("pseudoActif")!!

        Log.i("Volley","Création d'une liste pour l'utilisateur " + userId + intent.getStringExtra("pseudoActif"))
        val url = "http://tomnab.fr/todo-api/users/$userId/lists?label=$newTodoList"
        val headers = HashMap<String, String>()
        headers["hash"] = hash

        val request = object : StringRequest(Method.POST, url,
            Response.Listener { response ->
                apiCallGetLists(hash,pseudo)
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

    fun apiCallGetUser(pseudoActif : String) {

        Log.i("Volley", "Appel des Users ...")

        val url = "http://tomnab.fr/todo-api/users"
        val headers = HashMap<String, String>()
        headers["hash"] = getHashFromSharedPref()

        val request = object : StringRequest(Method.GET, url,
            Response.Listener<String> { response ->
                // Convertir la chaîne de caractères JSON en un objet JSON
                val jsonResponse = JSONObject(response)
                // Récupérer le hash du JSON
                val users = jsonResponse.getString("users")
                val gson = Gson()
                val listType = object : TypeToken<List<Map<String, String>>>() {}.type
                val list: List<Map<String, String>> = gson.fromJson(users, listType)
                var id = "1"
                for(el in list){
                    if(el["pseudo"]==pseudoActif){
                        Log.i("Volley", "Pseudo / pseudoActif :"+ el["id"] + " / "+ pseudoActif)
                        intent.putExtra("userId",el["id"].toString())
                        Log.i("Volley", "Pseudo  :"+ intent.getStringExtra("userId"))

                    }


                }
                Log.i("Volley", "Pseudo  :"+ intent.getStringExtra("userId"))
                Log.i("Volley", "Appel des users réussie : $users")
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
}