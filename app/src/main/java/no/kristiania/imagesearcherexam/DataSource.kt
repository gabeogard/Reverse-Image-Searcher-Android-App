package no.kristiania.imagesearcherexam

class DataSource{

    companion object{

        fun createDataSet(): ArrayList<RecyclerViewObjects>{
            val list = ArrayList<RecyclerViewObjects>()
            list.add(
                RecyclerViewObjects(
                    "Pikachu",
                    "https://assets.pokemon.com/assets/cms2/img/pokedex/full/025.png",

                )
            )
            list.add(
                RecyclerViewObjects(
                    "Snorlax",
                    "https://assets.pokemon.com/assets/cms2/img/pokedex/full/143.png",

                )
            )

            list.add(
                RecyclerViewObjects(
                    "Charizard",
                    "https://assets.pokemon.com/assets/cms2/img/pokedex/full/006.png",

                )
            )

            return list
        }
    }
}