package com.alaryani.epac.data.recyclerview.paging

import android.app.Application
import android.util.Log
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.alaryani.epac.data.model.response.MedicinesResponse
import com.alaryani.epac.data.repository.Repository
import com.alaryani.epac.ui.MainActivityViewModel
import com.alaryani.epac.util.Common
import com.alaryani.epac.util.Common.Companion.DetailFragment.CATEGORY_DETAIL
import com.alaryani.epac.util.Common.Companion.DetailFragment.PHARMACY_DETAIL
import retrofit2.HttpException
import retrofit2.Response
import java.io.IOException

class MedicineDataSource(
    private val repository: Repository,
    private val application: Application,
    private val detailFragment: Common.Companion.DetailFragment,
    private val id: Int,
    private val searchQuery: String,
    val lat: Double,
    val long: Double,
    val mainViewModel: MainActivityViewModel,
    val showOnlyDiscounted: Boolean,
    val sortCriteria: Int
) : PagingSource<Int, MedicinesResponse.MedicinesResponseItem>() {
    private val STARTING_KEY = 1
    private val PAGE_SIZE = 10

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, MedicinesResponse.MedicinesResponseItem> {
        val pageIndex = params.key ?: STARTING_KEY

        Log.i("TAG14", "load: pid $id  catid  page $pageIndex")

        try {
            val response: Response<MedicinesResponse> = when (detailFragment) {
                CATEGORY_DETAIL -> {
                    if (sortCriteria == -1) repository.getCategoryDetail(
                        id,
                        pageIndex,
                        PAGE_SIZE,
                        lat,
                        long,
                        null
                    )
                    else repository.getCategoryDetail(
                        id,
                        pageIndex,
                        PAGE_SIZE,
                        lat,
                        long,
                        sortCriteria
                    )
                }
                /*
                * SortCriteria - 0 = A to Z | 1 = Z to A | 2 = Low to High | 3 = High to Low
                * */
                PHARMACY_DETAIL -> {
                    if (sortCriteria == -1) repository.getPharmacyDetail(
                        id,
                        pageIndex,
                        PAGE_SIZE,
                        lat,
                        long,
                        showOnlyDiscounted,
                        null
                    ) else repository.getPharmacyDetail(
                        id,
                        pageIndex,
                        PAGE_SIZE,
                        lat,
                        long,
                        showOnlyDiscounted,
                        sortCriteria
                    )
                }
                Common.Companion.DetailFragment.MEDICINE_SEARCH -> {
                    repository.searchMedicine(searchQuery, pageIndex, PAGE_SIZE, lat, long)
                }
            }

            val item: MedicinesResponse? = response.body()

            if (item != null && detailFragment == PHARMACY_DETAIL) {

                var cartItem = mainViewModel.cartItemListLiveData.value

                cartItem?.forEach() {
                    if (it.pharmacyItem.id == mainViewModel.pharmacyDetailData?.id) {
                        it.medicineList.forEach() { cartItem ->
                            item.forEach() { medicinesResponseItem ->
                                run {
                                    if (cartItem.entityId == medicinesResponseItem.entityId) {
                                        medicinesResponseItem.quantity = cartItem.quantity
                                    }
                                }

                            }
                        }
                    }
                }
            }

            val nextKey =
                if (item == null || item.isEmpty()) {
                    null
                } else {
                    // By default, initial load size = 3 * NETWORK PAGE SIZE
                    // ensure we're not requesting duplicating items at the 2nd request
                    pageIndex + (params.loadSize / PAGE_SIZE)
                }

            if (pageIndex == STARTING_KEY) {
                mainViewModel.showLoading.postValue(false)
                if (detailFragment == PHARMACY_DETAIL) {
                    val headerModel =
                        MedicinesResponse.MedicinesResponseItem(
                            description = "",
                            medicineId = 0,
                            categoryID = 0,
                            entityId = 0,
                            imageHash = "",
                            imageUri = "",
                            name = "",
                            price = 0.0,
                            discountedPrice = 0.0,
                            unit = "",
                            quantity = 0,
                            type = 1,
                            stock = 0
                        );
                    item?.add(0, headerModel)

                }
            }

            Log.i("TAG14", "load:size ${params.loadSize} next key" + nextKey)


            return LoadResult.Page(
                data = item?.toList() ?: emptyList(),
                prevKey = if (pageIndex == STARTING_KEY) null else pageIndex,
                nextKey = nextKey
            )
        } catch (e: IOException) {
            return LoadResult.Error(e)
        } catch (exception: HttpException) {
            return LoadResult.Error(exception)
        }

    }

    override fun getRefreshKey(state: PagingState<Int, MedicinesResponse.MedicinesResponseItem>): Int? {
        // In our case we grab the item closest to the anchor position
        // then return its id - (state.config.pageSize / 2) as a buffer
        return state.anchorPosition?.let { anchorPosition ->
            state.closestPageToPosition(anchorPosition)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(anchorPosition)?.nextKey?.minus(1)
        }
    }
}