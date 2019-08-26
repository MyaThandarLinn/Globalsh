package g2sysnet.smart_gw.m_model

data class GetMessageUserModel (
    var NO_MESSAGE:String,
    var FG_ORG_RECEIVE:String,
    var CD_EMP_SEND:String,
    var CD_EMP_RECEIVE:String
)

//"NO_MESSAGE": "MSG2019071100001",
//"FG_ORG_RECEIVE": "E",
//"CD_EMP_SEND": "1101",
//"CD_EMP_RECEIVE": "1000"



//class DogModelAdapter(private val context: Context, private val dogsList: MutableList<DogModel>, private val listener: ItemClickListener) : RecyclerView.Adapter<DogModelAdapter.DogModelViewHolder>(), Filterable {
//
//    private var dogsSearchList: List<DogModel>? = null
//
//    inner class DogModelViewHolder(view: View) : RecyclerView.ViewHolder(view) {
//        var name_tv: TextView
//        var age_tv: TextView
//        var picture_iv: ImageView
//
//        init {
//            name_tv = view.findViewById(R.id.name)
//            age_tv = view.findViewById(R.id.age)
//            picture_iv = view.findViewById(R.id.picture)
//
//            view.setOnClickListener {
//
//                listener.onItemClicked(dogsSearchList!![adapterPosition])
//            }
//        }
//    }
//
//
//    init {
//        this.dogsSearchList = dogsList
//    }
//
//    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DogModelViewHolder {
//        val itemView = LayoutInflater.from(parent.context)
//            .inflate(R.layout.item_layout, parent, false)
//
//        return DogModelViewHolder(itemView)
//    }
//
//    override fun onBindViewHolder(holder: DogModelViewHolder, position: Int) {
//        val dog = dogsSearchList!![position]
//        holder.name_tv.text = dog.name
//        holder.age_tv.text = dog.age
//
//        var requestOptions = RequestOptions()
//        requestOptions = requestOptions.transforms(CenterCrop(), RoundedCorners(30))
//
//        Glide.with(context)
//            .load(dog.picture)
//            .apply(requestOptions)
//            .into(holder.picture_iv)
//    }
//
//    override fun getItemCount(): Int {
//        return dogsSearchList!!.size
//    }
//
//    override fun getFilter(): Filter {
//        return object : Filter() {
//            override fun performFiltering(charSequence: CharSequence): Filter.FilterResults {
//                val charString = charSequence.toString()
//                if (charString.isEmpty()) {
//                    dogsSearchList = dogsList
//                } else {
//                    val filteredList = ArrayList<DogModel>()
//                    for (row in dogsList) {
//
//                        if (row.name!!.toLowerCase().contains(charString.toLowerCase()) || row.age!!.contains(charSequence)) {
//                            filteredList.add(row)
//                        }
//                    }
//
//                    dogsSearchList = filteredList
//                }
//
//                val filterResults = Filter.FilterResults()
//                filterResults.values = dogsSearchList
//                return filterResults
//            }
//
//            override fun publishResults(charSequence: CharSequence, filterResults: Filter.FilterResults) {
//                dogsSearchList = filterResults.values as ArrayList<DogModel>
//                notifyDataSetChanged()
//            }
//        }
//    }
//
//    interface ItemClickListener {
//        fun onItemClicked(dog: DogModel)
//    }
//}


