package com.andromeda.immicart.delivery.furniture


import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

import com.andromeda.immicart.R
import kotlinx.android.synthetic.main.fragment_sub_category_furniture.*

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [SubCategoryFurnitureFragment.newInstance] factory method to
 * create an instance of this fragment.
 *
 */
class SubCategoryFurnitureFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_sub_category_furniture, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)


        val list = ArrayList<Department>()
        val listFurniture = ArrayList<Furniture>()
        val department = Department(R.drawable.sofa_2, "Dining")
        val department1 = Department(R.drawable.sofa, "Living Room")
        val department2 = Department(R.drawable.sofa_1, "Bedroom")
        val department3 = Department(R.drawable.sofa_7, "Office")
        val department4 = Department(R.drawable.sofa_6, "Baby")
        list.add(department)
        list.add(department1)
        list.add(department2)
        list.add(department3)
        list.add(department4)
        vertical_department_recycler_view?.let {
            it.layoutManager = LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false)
            it.adapter = HorizontalCategoryRecyclerAdapter(list, requireActivity(), { department: Department -> departmentClicked(department) })
        }

        val furniture = Furniture(R.drawable.sofa_6, "Blue Velvet 2 Seater Sofa", "40000")
        listFurniture.add(furniture)
        listFurniture.add(furniture)
        listFurniture.add(furniture)
        listFurniture.add(furniture)
        listFurniture.add(furniture)
        listFurniture.add(furniture)
        listFurniture.add(furniture)
        listFurniture.add(furniture)
        products_items_recycler?.let {
            it.isNestedScrollingEnabled = false
            it.layoutManager = GridLayoutManager(requireActivity(), 2)
            it.adapter = FurnitureRecyclerAdapter(listFurniture, requireActivity(), { furniture: Furniture -> furnitureClicked(furniture) })
        }
    }

    fun furnitureClicked(furniture: Furniture) {

        startActivity(Intent(requireContext(), FurnitureProductPageActivity::class.java))

    }

    fun departmentClicked(furniture: Department) {
        findNavController().navigate(R.id.action_subCategoryFurnitureFragment_to_categoryFurnitureProductsFragment)

//        startActivity(Intent(requireContext(), FurnitureProductPageActivity::class.java))

    }



    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment SubCategoryFurnitureFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            SubCategoryFurnitureFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}
