package com.pine.pinedroid.db.bean

import com.pine.pinedroid.db.ModelK
import com.pine.pinedroid.db.model
import com.pine.pinedroid.utils.log.loge


open class Relation<T : BaseDataTable>(val foreignKey: String)

class HasMany<T : BaseDataTable>(foreignKey: String) : Relation<T>(foreignKey)
class BelongsTo<T : BaseDataTable>(foreignKey: String) : Relation<T>(foreignKey)
class HasOne<T : BaseDataTable>(foreignKey: String) : Relation<T>(foreignKey)

interface DbRelation {
    var relations: Map<String, Relation<out BaseDataTable>>

    fun <T : BaseDataTable> Relation<T>.getRelation(): ModelK<T>? {

        return null
    }

}