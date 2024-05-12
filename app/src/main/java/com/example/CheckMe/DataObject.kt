package com.example.CheckMe

object DataObject {
    var listdata = mutableListOf<Entity>()

    fun setData(title: String, priority: String, dueDate: Long) {
        listdata.add(Entity(0, title, priority, dueDate))
    }

    fun getAllData(): List<Entity> {
        return listdata
    }

    fun deleteAll(){
        listdata.clear()
    }

    fun getData(pos: Int): Entity {
        return listdata[pos]
    }

    fun deleteData(pos: Int){
        listdata.removeAt(pos)
    }

    fun updateData(pos: Int, title: String, priority: String, dueDate: Long) {
        val entity = listdata[pos]
        listdata[pos] = entity.copy(title = title, priority = priority, dueDate = dueDate)
    }
}
