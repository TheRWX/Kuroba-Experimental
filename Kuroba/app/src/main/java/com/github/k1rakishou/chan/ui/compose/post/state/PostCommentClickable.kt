package com.github.k1rakishou.chan.ui.compose.post.state

import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Path

import com.github.k1rakishou.chan.core.parser.TextPartSpan

@Immutable
sealed interface PostCommentClickable {
  fun intersectsWith(textOffset: Int): Boolean

  @Immutable
  data class Spoiler(
    val start: Int,
    val end: Int
  ) : PostCommentClickable {
    override fun intersectsWith(textOffset: Int): Boolean {
      return textOffset in start..end
    }
  }

  @Immutable
  data class Link(
    val start: Int,
    val end: Int,
    val path: Path,
    val linkable: TextPartSpan.Linkable? = null
  ) : PostCommentClickable {
    override fun intersectsWith(textOffset: Int): Boolean {
      return textOffset in start..end
    }
  }

}

