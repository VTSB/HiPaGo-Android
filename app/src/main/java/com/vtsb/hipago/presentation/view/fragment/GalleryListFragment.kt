package com.vtsb.hipago.presentation.view.fragment

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.res.ColorStateList
import android.os.Bundle
import android.util.Log
import android.util.TypedValue
import android.view.*
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.AutoCompleteTextView
import androidx.appcompat.widget.SearchView
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.Navigation
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearSmoothScroller
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.SmoothScroller
import com.google.android.material.internal.NavigationMenuItemView
import com.google.android.material.navigation.NavigationView
import com.google.common.collect.BiMap
import com.vtsb.hipago.R
import com.vtsb.hipago.databinding.FragmentGalleryListBinding
import com.vtsb.hipago.domain.entity.GalleryBlock
import com.vtsb.hipago.domain.entity.GalleryBlockType
import com.vtsb.hipago.domain.entity.TagType
import com.vtsb.hipago.presentation.view.MainActivity
import com.vtsb.hipago.presentation.view.adapter.GalleryListAdapter
import com.vtsb.hipago.presentation.view.adapter.SearchCursorAdapter
import com.vtsb.hipago.presentation.view.custom.listener.RecyclerItemClickListener
import com.vtsb.hipago.presentation.viewmodel.GalleryListViewModel
import com.vtsb.hipago.util.Constants.PREFETCH_PAGE
import com.vtsb.hipago.util.converter.QueryConverter
import com.vtsb.hipago.util.converter.TagConverter
import dagger.hilt.android.AndroidEntryPoint
import java.util.*
import javax.inject.Inject
import javax.inject.Named
import kotlin.math.abs
import kotlin.properties.Delegates

@AndroidEntryPoint
class GalleryListFragment : NavigationView.OnNavigationItemSelectedListener, Fragment() {

    private val viewModel: GalleryListViewModel by viewModels()
    @Inject lateinit var tagConverter: TagConverter
    @Inject lateinit var queryConverter: QueryConverter
    @Inject @Named("stringTypeBiMap") lateinit var stringTypeBiMap: BiMap<String, TagType>

    private lateinit var adapter: GalleryListAdapter
    private lateinit var recyclerView: RecyclerView
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navigationView: NavigationView

    private lateinit var startScroller: SmoothScroller

    private var colorPrimary by Delegates.notNull<Int>()
    private var loadModeItem: NavigationMenuItemView? = null
    private var languageItem: NavigationMenuItemView? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {

        val galleryListFragmentArgs: GalleryListFragmentArgs = GalleryListFragmentArgs.fromBundle(requireArguments())
        val query = galleryListFragmentArgs.query
        viewModel.setQuery(query)

        adapter = GalleryListAdapter(viewModel)
        adapter.setHasStableIds(true)


        colorPrimary = getColor(android.R.attr.textColorPrimary)

        val binding = FragmentGalleryListBinding.inflate(inflater, container, false)
        recyclerView = binding.galleryList
        drawerLayout = binding.drawerLayout
        navigationView = binding.navView

        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner


        recyclerView.adapter = adapter
        recyclerView.setHasFixedSize(true)
        recyclerView.setItemViewCacheSize(25)
        recyclerView.addOnScrollListener(object: RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)

                val manager = recyclerView.layoutManager as LinearLayoutManager? ?: return

                // top
                val firstPosition = manager.findFirstVisibleItemPosition()
                if (firstPosition == -1) return

                val firstGalleryBlock: GalleryBlock = viewModel.getGalleryBlockList()[firstPosition]
                val firstPage: Int = viewModel.getGalleryIdPageMap()[firstGalleryBlock.id] ?: return

                viewModel.nowPage.value = firstPage

                if (firstPage <= viewModel.getTPage() + PREFETCH_PAGE) {
                    viewModel.loadTopPage()
                }

                // bottom
                val lastPosition = manager.findLastVisibleItemPosition()
                val lastGalleryBlock: GalleryBlock =
                    viewModel.getGalleryBlockList()[lastPosition]
                val lastPage: Int = viewModel.getGalleryIdPageMap()[lastGalleryBlock.id] ?: return

                if (lastPage >= viewModel.getBPage() - PREFETCH_PAGE) {
                    viewModel.loadBottomPage()
                }
            }
        })
        recyclerView.addOnItemTouchListener(RecyclerItemClickListener(requireContext(), object: RecyclerItemClickListener.OnItemClickListener.Normal {
            override fun onItemClick(view: View, position: Int) {
                val galleryBlock = viewModel.getGalleryBlockList()[position]
                if (galleryBlock.type != GalleryBlockType.LOADING &&
                    galleryBlock.type != GalleryBlockType.FAILED) {
//                    Navigation.findNavController(view).navigate(
//                        GalleryListFragmentDirections
//                            .actionGalleryListFragmentToGalleryInformationFragment(galleryBlock)
//                    )
                }
            }

            override fun onItemLongClick(view: View, position: Int) {
                val items = arrayOf("읽기", "다운로드", "북마크", "필터링", "리로드")
                val dialog = AlertDialog.Builder(view.context)
                val galleryBlock = viewModel.getGalleryBlockList()[position]

                dialog.setTitle(galleryBlock.title)
                    .setItems(
                        items
                    ) { _: DialogInterface?, which: Int ->
                        when(which) {
//                          0->
//                                Navigation.findNavController(view).navigate(
//                                    GalleryListFragmentDirections.actionGalleryListFragmentToReaderScrollFragment(
//                                        viewModel.getLateInitGalleryInfo(galleryBlock)
//                                    )
//                                )
//                            1-> {
//                                Toast.makeText(
//                                    view.context, R.string.gallery_download_start, Toast.LENGTH_SHORT).show()
//                                viewModel.downloadAllImages(galleryBlock) { parameter ->
//                                    Toast.makeText(
//                                        view.context,
//                                        String.format(
//                                            view.resources.getString(R.string.gallery_info_loading_failed_num),
//                                            galleryBlock.getNo()
//                                        ),
//                                        Toast.LENGTH_LONG
//                                    ).show()
//                                    null
//                                }
//                            }
//                            2-> // todo: bookmark
//                            3-> //todo: filtering
                            4-> viewModel.reloadGalleryBlock(galleryBlock.id, position)
                        }
                    }.create().show()

            }
        }))
        startScroller = object : LinearSmoothScroller(requireContext()) {
            override fun getVerticalSnapPreference(): Int = SNAP_TO_START
        }


        // https://stackoverflow.com/questions/1489852/android-handle-enter-in-an-edittext/43172342
        binding.fglPageEdit.clearFocus()
        binding.fglPageEdit.setOnEditorActionListener { v, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_GO) {
                var text = v.text.toString()
                val idx = text.indexOf('.')
                text = if (idx == -1) text else text.substring(0, idx)
                var toPage = text.toInt()
                if (toPage >= viewModel.getTPage() && toPage <= viewModel.getBPage()) {
                    if (abs(toPage - viewModel.nowPage.value!!) > 2) {
                        teleportPage(toPage)
                    } else {
                        movePage(toPage)
                    }
                } else {
                    toPage =
                        1.coerceAtLeast(
                            toPage.coerceAtMost(
                                viewModel.maxPage.value!!
                            )
                        )
                    viewModel.changePage(toPage)
                }

                // https://devuryu.tistory.com/286
                val inputMethodManager =
                    (v.context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager)
                inputMethodManager.toggleSoftInput(
                    InputMethodManager.HIDE_IMPLICIT_ONLY,
                    0
                )

                // https://stackoverflow.com/questions/5056734/android-force-edittext-to-remove-focus
                v.clearFocus()
                return@setOnEditorActionListener true
            }
            false
        }

        setHasOptionsMenu(true)
        val activity: MainActivity = requireActivity() as MainActivity

        activity.setSupportActionBar(binding.toolbar)


        binding.navView.inflateMenu(if (query.isNotEmpty()) R.menu.drawer_language else R.menu.drawer)

        val mAppBarConfiguration =
            AppBarConfiguration.Builder().setOpenableLayout(binding.drawerLayout).build()
        val navController = Navigation.findNavController(activity, R.id.nav_host_fragment)
        NavigationUI.setupActionBarWithNavController(activity, navController, mAppBarConfiguration)
        NavigationUI.setupWithNavController(binding.navView, navController)

        binding.toolbar.setNavigationIcon(R.drawable.ic_baseline_menu_24)
        binding.navView.setNavigationItemSelectedListener(this)

        changeSelectedColor()

        if (query.isEmpty()) binding.toolbar.setTitle(R.string.app_name)
        else binding.toolbar.title = tagConverter.toLocalQueryJust(query)

        return binding.root
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.gallery_list, menu)

        val menuItem = menu.findItem(R.id.glm_search_bar)
        val searchView = menuItem.actionView as SearchView

        val searchAutoCompleteTextView = searchView.findViewById<AutoCompleteTextView>(androidx.appcompat.R.id.search_src_text)
        searchAutoCompleteTextView.threshold = 0
        searchView.suggestionsAdapter = SearchCursorAdapter(
            viewModel.searchResultGetter, stringTypeBiMap, searchView.context, null)
        searchView.setOnSuggestionListener(object : SearchView.OnSuggestionListener {
            override fun onSuggestionSelect(position: Int): Boolean {
                return false
            }

            override fun onSuggestionClick(position: Int): Boolean {
                val cursor = searchView.suggestionsAdapter.cursor
                cursor.moveToPosition(position)
                val tag = cursor.getString(1)
                val before = cursor.getString(4)
                val nowQuery = searchView.query.toString()
                val splitter: Char = queryConverter.getChar()
                val adder = if (splitter == ' ') " " else "$splitter "
                val transformedTag: String = queryConverter.transformNotSplitAble(tag)
                val idx = nowQuery.lastIndexOf(splitter)
                var newQuery = if (idx == -1) "" else nowQuery.substring(0, idx) + adder
                newQuery += if (before == null) "$transformedTag:" else "$before:$transformedTag$adder"
                searchView.setQuery(newQuery, false)
                return true
            }
        })
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                Navigation.findNavController(requireView()).navigate(
                    GalleryListFragmentDirections
                        .actionGalleryListFragmentSelf()
                        .setQuery(query)
                )
                return false
            }
            override fun onQueryTextChange(newText: String): Boolean = false
        })

        super.onCreateOptionsMenu(menu, inflater)

        searchView.setQuery(viewModel.getQuery(), false)
        viewModel.init(adapter.listener)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home-> {
                drawerLayout.openDrawer(GravityCompat.START)
                changeSelectedColor()
            }
//            R.id.glm_settings->
//                Navigation.findNavController(requireView()).navigate(
//                    GalleryListFragmentDirections
//                        .actionGalleryListFragmentToSettingFragment()
//                )
//            R.id.glm_bookmark-> {
//
//            }
//            R.id.glm_filtering-> Navigation.findNavController(requireView()).navigate(
//                GalleryListFragmentDirections
//                    .actionGalleryListFragmentToFilterFragment()
//            )
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        TODO("Not yet implemented")
    }

    @SuppressLint("RestrictedApi")
    private fun changeSelectedColor() {
        loadModeItem?.setTextColor(ColorStateList.valueOf(colorPrimary))
        languageItem?.setTextColor(ColorStateList.valueOf(colorPrimary))
        loadModeItem = when (viewModel.getQuery()) {
            "index" -> navigationView.findViewById(R.id.nav_recent)
            "popular" -> navigationView.findViewById(R.id.nav_popularity)
            "[recently_watched]" -> navigationView.findViewById(R.id.nav_recent_watched)
            "type:doujinshi" -> navigationView.findViewById(R.id.nav_type_doujinshi)
            "type:manga" -> navigationView.findViewById(R.id.nav_type_manga)
            "type:gamecg" -> navigationView.findViewById(R.id.nav_type_gamecg)
            "type:artistcg" -> navigationView.findViewById(R.id.nav_type_artistcg)
            "type:anime" -> navigationView.findViewById(R.id.nav_type_anime)
            else -> null
        }
        languageItem = null
        when (viewModel.getLanguage()) {
            "all" -> languageItem = navigationView.findViewById(R.id.nav_lang_all)
            "korean" -> languageItem = navigationView.findViewById(R.id.nav_lang_korean)
            "english" -> languageItem = navigationView.findViewById(R.id.nav_lang_english)
            "japanese" -> languageItem = navigationView.findViewById(R.id.nav_lang_japanese)
        }

        val c = getColor(R.color.colorMain)
        if (loadModeItem != null) {
            loadModeItem!!.setTextColor(ColorStateList.valueOf(c))
        }
        if (languageItem != null) {
            languageItem!!.setTextColor(ColorStateList.valueOf(c))
        }
    }

    private fun movePage(page: Int) {
        startScroller.targetPosition = viewModel.getPagePositionMap()[page]!!
        recyclerView.layoutManager?.startSmoothScroll(startScroller)
    }

    private fun teleportPage(page: Int) {
        (recyclerView.layoutManager as LinearLayoutManager)
            .scrollToPositionWithOffset(page, 0)
    }

    private fun getColor(resId: Int): Int {
        val typedValue = TypedValue()
        val theme = requireActivity().theme
        theme.resolveAttribute(resId, typedValue, true)
        val arr = requireActivity().obtainStyledAttributes(typedValue.data, intArrayOf(resId))
        val color = arr.getColor(0, -1)
        arr.recycle()
        return color
    }



}