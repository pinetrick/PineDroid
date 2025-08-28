//package com.pine.pinedroid.db.bean
//
//import com.pine.pinedroid.db.Model
//import com.pine.pinedroid.db.ModelK
//import com.pine.pinedroid.db.model
//import kotlin.reflect.KClass
//
//
//open class RelationX<T : BaseDataTable>(
//    private val baseDataTable: BaseDataTable,
//    private val modelClass: KClass<T>,
//    val foreignKey: String
//) {
//    fun getModelK(): ModelK<T> {
//        return ModelK(modelClass, baseDataTable._dbName)
//    }
//}
//
//
//class HasMany<T : BaseDataTable>(
//    private val baseDataTable: BaseDataTable,
//    modelClass: KClass<T>,
//    foreignKey: String): RelationX<T>(baseDataTable, modelClass, foreignKey) {
//    fun query(): ModelK<T>{
//        return getModelK().where(foreignKey, baseDataTable.id)
//    }
//}
//class BelongsTo<T: BaseDataTable>(baseDataTable: BaseDataTable,modelClass: KClass<T>, foreignKey: String): RelationX<T>(baseDataTable, modelClass,foreignKey)
//class HasOne<T: BaseDataTable>(baseDataTable: BaseDataTable, modelClass: KClass<T>,foreignKey: String): RelationX<T>(baseDataTable, modelClass,foreignKey)
//
//
