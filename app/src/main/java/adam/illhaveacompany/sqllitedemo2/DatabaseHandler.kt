package adam.illhaveacompany.sqllitedemo2

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteException
import android.database.sqlite.SQLiteOpenHelper

class DatabaseHandler (context: Context) :
    SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        //changed when I want to add a column
        private const val DATABASE_VERSION = 1
        private const val DATABASE_NAME = "EmployeeDatabase"

        //we're only using one table - contacts
        private const val TABLE_CONTACTS = "EmployeeTable"

        //these are the keys of the contacts table
        private const val KEY_ID = "_id"
        private const val KEY_NAME = "name"
        //*I'd exclude this and change KEY_NAME to KEY_PICTURE -- BLOB is a blob of data - might be my picture
        private const val KEY_EMAIL = "email"

    }

    override fun onCreate(db: SQLiteDatabase?) {
        //creating the table with the fields -- i might need a space after INTEGER PRIMARY KEY, TEXT
        val CREATE_CONTACTS_TABLE = ("CREATE TABLE " + TABLE_CONTACTS + "("
                + KEY_ID + " INTEGER PRIMARY KEY," + KEY_NAME + " TEXT,"
                + KEY_EMAIL + " TEXT" + ")")
        //pushes the code through
        db?.execSQL(CREATE_CONTACTS_TABLE)

        //can also be written as db?.execSQL("CREATE TABLE " + TABLE_CONTACTS + "("
        //                + KEY_ID + " INTEGER PRIMARY KEY," + KEY_NAME + " TEXT,"
        //                + KEY_EMAIL + " TEXT" + ")")

    }
    //2 - 159

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        // if I update it, drop the entire table -- shows in video 160
        db!!.execSQL("DROP TABLE IF EXISTS " + TABLE_CONTACTS)
        onCreate(db)
    }//3


    //in the arguments we tell it which employee we want to add
    fun addEmployee(emp: EmpModelClass): Long {
        val db = this.writableDatabase

        val contentValues = ContentValues()
        //puts the KEY_NAME as the name from emp: From the EmpModelClass
        contentValues.put(KEY_NAME, emp.name)
        contentValues.put(KEY_EMAIL, emp.email)

        //inserting row - the ID automatically increments
        val success = db.insert(TABLE_CONTACTS, null, contentValues)

        db.close() //closes the database connection
        return success // returns the row

    }//4

    //selects all employees from TABLE_CONTACTS
    fun viewEmployee() : ArrayList<EmpModelClass> {

        //EmpModelClass is just a database row of an employee
        val empList: ArrayList<EmpModelClass> = ArrayList<EmpModelClass>()

        val selectQuery = "SELECT * FROM $TABLE_CONTACTS"

        val db = this.readableDatabase
        var cursor: Cursor? = null

        try {
            cursor = db.rawQuery(selectQuery, null)
        }catch (e: SQLiteException){
            db.execSQL(selectQuery)
            return ArrayList()
        }//5

        var id: Int
        var name: String
        var email: String
        //6

        //the cursor is just the selector of id's
        if (cursor != null) {
            if(cursor.moveToFirst()) {
                do{
                    id = cursor.getInt(cursor.getColumnIndex(KEY_ID))
                    name = cursor.getString(cursor.getColumnIndex(KEY_NAME))
                    email = cursor.getString(cursor.getColumnIndex(KEY_EMAIL))

                    val emp = EmpModelClass(id = id, name = name, email = email)

                } while(cursor.moveToNext())
            }
        }
        return empList
    }//7

    fun updateEmployee(emp: EmpModelClass) : Int {
        //declaring what the db is in every function we use it in
        val db = this.writableDatabase
        val contentValues = ContentValues()
        contentValues.put(KEY_NAME, emp.name)
        contentValues.put(KEY_EMAIL, emp.email)

        //updating row
        val success = db.update(TABLE_CONTACTS, contentValues, KEY_ID + "=" + emp.id, null)

        db.close()
        //returns the updated row
        return success
        //can be written as return db.update(TABLE_CONTACTS, , contentValues, KEY_ID + "=" + emp.id, null)

    }//8

    fun deleteEmployee(emp: EmpModelClass): Int {
        val db = this.writableDatabase
        val contentValues = ContentValues()
        //tell it which ID you want to delete
        contentValues.put(KEY_ID, emp.id)

        val success = db.delete(TABLE_CONTACTS, KEY_ID + "=" + emp.id, null)

        db.close()
        return success
    }//9


}