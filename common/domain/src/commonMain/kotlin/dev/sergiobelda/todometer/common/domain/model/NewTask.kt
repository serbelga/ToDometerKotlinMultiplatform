package dev.sergiobelda.todometer.common.domain.model

// TODO: Consider using/modeling a UseCase Params class.
data class NewTask(
    val title: String,
    val tag: Tag = Tag.GRAY,
    val description: String? = null,
    val dueDate: Long? = null,
    val taskChecklistItems: List<String> = emptyList(),
)
