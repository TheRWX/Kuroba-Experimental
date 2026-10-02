package com.github.k1rakishou.chan.ui.controller.popup

import android.content.Context
import android.util.LruCache
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.github.k1rakishou.chan.R
import com.github.k1rakishou.chan.core.compose.AsyncData
import com.github.k1rakishou.chan.core.di.component.activity.ActivityComponent
import com.github.k1rakishou.chan.core.parser.PostViewMode
import com.github.k1rakishou.chan.ui.cell.PostCellData
import com.github.k1rakishou.chan.ui.cell.PostCellInterface
import com.github.k1rakishou.chan.ui.compose.components.KurobaComposeCard
import com.github.k1rakishou.chan.ui.compose.components.KurobaComposeErrorMessage
import com.github.k1rakishou.chan.ui.compose.components.KurobaComposeText
import com.github.k1rakishou.chan.ui.compose.components.KurobaSearchInput
import com.github.k1rakishou.chan.ui.compose.ktu
import com.github.k1rakishou.chan.ui.compose.lazylist.LazyColumnWithFastScroller
import com.github.k1rakishou.chan.ui.compose.post.state.PostDisplayOptions
import com.github.k1rakishou.chan.ui.compose.post.state.postDisplayOptionsForRepliesPopup
import com.github.k1rakishou.chan.ui.compose.post.ui.PostCellUi
import com.github.k1rakishou.chan.ui.compose.providers.LocalChanTheme
import com.github.k1rakishou.chan.ui.compose.replaceWithText
import com.github.k1rakishou.chan.ui.compose.textAsFlow
import com.github.k1rakishou.chan.ui.helper.PostPopupHelper
import com.github.k1rakishou.common.AndroidUtils
import com.github.k1rakishou.common.AppConstants
import com.github.k1rakishou.common.ModularResult
import com.github.k1rakishou.common.StringUtils
import com.github.k1rakishou.common.isNotNullNorBlank
import com.github.k1rakishou.common.mutableListWithCap
import com.github.k1rakishou.core_logger.Logger
import com.github.k1rakishou.core_themes.ThemeEngine
import com.github.k1rakishou.model.data.descriptor.ChanDescriptor
import com.github.k1rakishou.model.data.descriptor.PostDescriptor
import com.github.k1rakishou.model.data.post.ChanPost
import com.github.k1rakishou.model.data.post.ChanPostImage
import com.github.k1rakishou.model.data.post.PostIndexed
import com.github.k1rakishou.model.util.ChanPostUtils
import com.github.k1rakishou.persist_state.IndexAndTop
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.isActive
import kotlinx.coroutines.withContext
import java.util.Locale

class PostSearchPopupController(
  context: Context,
  postPopupHelper: PostPopupHelper,
  postCellCallback: PostCellInterface.PostCellCallback,
  private var initialQuery: String? = null
) : BasePostPopupController<PostSearchPopupController.PostSearchPopupData>(context, postPopupHelper, postCellCallback) {

  private val indexedPosts = mutableListOf<PostIndexed>()
  private val searchTextFieldState = TextFieldState(initialText = initialQuery ?: "")
  private val currentQueryState = mutableStateOf(initialQuery ?: "")
  private val totalFoundCountState = mutableIntStateOf(0)

  override val postPopupType: PostPopupType
    get() = PostPopupType.Search
  override val postDisplayOptions: PostDisplayOptions
    get() = postDisplayOptionsForRepliesPopup()

  override fun displayData(chanDescriptor: ChanDescriptor, data: PostPopupHelper.PostPopupData) {
    super.displayData(chanDescriptor, data)

    val prevQuery = initialQuery ?: getLastQuery(chanDescriptor)
    initialQuery = null

    if (prevQuery.isNotEmpty() && searchTextFieldState.text.isEmpty()) {
      searchTextFieldState.edit {
        replaceWithText(prevQuery)
      }
    }
  }

  @Composable
  override fun BoxScope.Content() {
    val displayingAsyncData by displayingAsyncDataState
    val lazyListState = rememberLazyListState()
    val chanTheme = LocalChanTheme.current

    val kurobaSearchInputColor = if (ThemeEngine.isDarkColor(chanTheme.backColor)) {
      Color.LightGray
    } else {
      Color.DarkGray
    }

    val currentQuery by currentQueryState
    val totalFoundCount by totalFoundCountState

    LaunchedEffect(key1 = searchTextFieldState, key2 = displayingData?.descriptor) {
      val descriptor = displayingData?.descriptor ?: return@LaunchedEffect
      searchTextFieldState.textAsFlow()
        .debounce(250L)
        .onEach { queryCharSequence ->
          val query = queryCharSequence.toString()
          storeQuery(descriptor, query)
          performSearch(descriptor, query)
        }
        .collect()
    }

    KurobaComposeCard {
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .wrapContentHeight()
      ) {
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .padding(horizontal = 8.dp, vertical = 4.dp),
          verticalAlignment = Alignment.CenterVertically
        ) {
          KurobaSearchInput(
            modifier = Modifier
              .weight(1f)
              .wrapContentHeight(),
            color = kurobaSearchInputColor,
            searchQueryState = searchTextFieldState
          )

          val totalFoundText = if (currentQuery.length < AppConstants.MIN_QUERY_LENGTH) {
            stringResource(R.string.search_found_unknown)
          } else {
            stringResource(R.string.search_found_count, totalFoundCount)
          }

          KurobaComposeText(
            modifier = Modifier.padding(start = 8.dp, end = 4.dp),
            text = totalFoundText,
            color = chanTheme.textColorHintCompose,
            fontSize = 12.ktu
          )
        }

        LazyColumnWithFastScroller(
          modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .padding(horizontal = 8.dp, vertical = 4.dp)
            .animateContentSize(),
          state = lazyListState,
          content = {
            val localDisplayingData = displayingAsyncData
            if (localDisplayingData is AsyncData.Error) {
              item(
                key = "error_state",
                contentType = "error_state",
                content = {
                  KurobaComposeErrorMessage(
                    modifier = Modifier
                      .fillMaxWidth()
                      .heightIn(min = 160.dp),
                    error = localDisplayingData.throwable
                  )
                }
              )
              return@LazyColumnWithFastScroller
            }

            if (threadState.postCellStates.isEmpty() && currentQuery.length >= AppConstants.MIN_QUERY_LENGTH) {
              item(
                key = "no_results",
                contentType = "no_results",
                content = {
                  Box(
                    modifier = Modifier
                      .fillMaxWidth()
                      .heightIn(min = 120.dp),
                    contentAlignment = Alignment.Center
                  ) {
                    KurobaComposeText(
                      text = stringResource(R.string.search_found_count, 0),
                      color = chanTheme.textColorHintCompose
                    )
                  }
                }
              )
              return@LazyColumnWithFastScroller
            }

            items(
              count = threadState.postCellStates.size,
              key = { index -> threadState.postCellStates[index].postDescriptor },
              contentType = { "posts_state" },
              itemContent = { postIndex ->
                val postCellState = threadState.postCellStates[postIndex]
                PostCellUi(
                  modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight()
                    .padding(vertical = 8.dp, horizontal = 4.dp),
                  postCellState = postCellState,
                  threadState = threadState
                )
              }
            )
          }
        )
      }
    }
  }

  private suspend fun performSearch(chanDescriptor: ChanDescriptor, query: String) {
    val searchQuery = query.toLowerCase(Locale.ENGLISH)

    val resultPosts = withContext(Dispatchers.Default) {
      val posts = mutableListWithCap<ChanPost>(128)

      chanThreadManager.get().iteratePostsWhile(chanDescriptor) { chanPost ->
        if (!isActive) {
          return@iteratePostsWhile false
        }

        if (query.length < AppConstants.MIN_QUERY_LENGTH) {
          posts += chanPost.deepCopy()
          return@iteratePostsWhile true
        }

        if (matchesQuery(chanPost, searchQuery)) {
          posts += chanPost.deepCopy()
          return@iteratePostsWhile true
        }

        return@iteratePostsWhile true
      }

      return@withContext posts
    }

    val retainedPosts = postHideHelper.get().processPostFilters(chanDescriptor, resultPosts, mutableSetOf())
      .safeUnwrap { error ->
        Logger.e(TAG, "postHideHelper.filterHiddenPosts error", error)
        return
      }

    val indexed = mutableListWithCap<PostIndexed>(retainedPosts.size)
    for ((index, retainedPost) in retainedPosts.withIndex()) {
      indexed.add(PostIndexed(retainedPost, index))
    }

    this@PostSearchPopupController.indexedPosts.clear()
    this@PostSearchPopupController.indexedPosts.addAll(indexed)

    totalFoundCountState.intValue = retainedPosts.size
    currentQueryState.value = query

    threadState.updatePosts(
      chanDescriptor = chanDescriptor,
      posts = retainedPosts,
      postViewMode = PostViewMode.List,
      preloadStartPosition = 0,
      forced = true,
      replaceExisting = true
    )
    threadState.setSearchQuery(query.takeIf { it.length >= AppConstants.MIN_QUERY_LENGTH })
  }

  override fun cleanup() {
    indexedPosts.clear()
  }

  override fun getDisplayingPostDescriptors(): List<PostDescriptor> {
    if (indexedPosts.isEmpty()) {
      return emptyList()
    }

    val postDescriptors: MutableList<PostDescriptor> = ArrayList()
    for (postIndexed in indexedPosts) {
      postDescriptors.add(postIndexed.chanPost.postDescriptor)
    }

    return postDescriptors
  }

  override fun injectActivityDependencies(component: ActivityComponent) {
    component.inject(this)
  }

  override fun onImageIsAboutToShowUp() {
    if (this.view.focusedChild != null) {
      val currentFocus = this.view.focusedChild
      AndroidUtils.hideKeyboard(currentFocus)
      currentFocus.clearFocus()
    }
  }

  private fun matchesQuery(chanPost: ChanPost, query: String): Boolean {
    if (chanPost.postComment.originalComment().contains(query, ignoreCase = true)) {
      return true
    }

    if (chanPost.postDescriptor.postNo.toString().contains(query, ignoreCase = true)) {
      return true
    }

    if (chanPost.subject?.contains(query, ignoreCase = true) == true) {
      return true
    }

    if (chanPost.name?.contains(query, ignoreCase = true) == true) {
      return true
    }

    if (chanPost.postImages.isNotEmpty()) {
      for (postImage in chanPost.postImages) {
        val filename = formatImageInfoForSearch(postImage)
        if (filename.contains(query, ignoreCase = true)) {
          return true
        }
      }
    }

    return false
  }

  private fun formatImageInfoForSearch(chanPostImage: ChanPostImage): String {
    return buildString {
      if (chanPostImage.serverFilename.isNotNullNorBlank()) {
        append(chanPostImage.serverFilename)
        append(' ')
      }

      if (chanPostImage.filename.isNotNullNorBlank()) {
        append(chanPostImage.filename)
        append(' ')
      }

      if (chanPostImage.extension.isNotNullNorBlank()) {
        append(chanPostImage.extension!!.toUpperCase(Locale.ENGLISH))
        append(' ')
      }

      append(StringUtils.UNBREAKABLE_SPACE_SYMBOL)
      append("${chanPostImage.imageWidth}x${chanPostImage.imageHeight}")

      append(StringUtils.UNBREAKABLE_SPACE_SYMBOL)
      append(ChanPostUtils.getReadableFileSize(chanPostImage.size)
        .replace(' ', StringUtils.UNBREAKABLE_SPACE_SYMBOL))
    }
  }

  private fun getLastQuery(chanDescriptor: ChanDescriptor): String {
    return lastQueryCache.get(chanDescriptor) ?: ""
  }

  private fun storeQuery(chanDescriptor: ChanDescriptor, query: String) {
    lastQueryCache.put(chanDescriptor, query)
  }

  data class PostSearchPopupData(
    override val descriptor: ChanDescriptor,
    override val popupControllerType: PostCellData.PopupControllerType
  ) : PostPopupHelper.PostPopupData

  companion object {
    private const val TAG = "PostSearchPopupController"

    val scrollPositionCache = LruCache<ChanDescriptor, IndexAndTop>(128)

    private val lastQueryCache = LruCache<ChanDescriptor, String>(128)
  }

}