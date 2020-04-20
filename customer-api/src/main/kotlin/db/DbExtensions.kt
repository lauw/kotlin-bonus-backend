package db

import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.ResultRow


fun ResultRow.ifContains(column: Column<*>, block: (Any) -> Unit = {}) {
    this.getOrNull(column)?.let {
        block(it)
    }
}