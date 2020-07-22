package adam.illhaveacompany.sqllitedemo2

import android.app.Dialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        btnAdd.setOnClickListener{
            addRecord(view)
        }

        setupListofDataIntoRecyclerView()//10

    }

    fun addRecord(view: View) {
        val name = etName.text.toString()
        val email = etEmailId.text.toString()
        val databaseHandler: DatabaseHandler = DatabaseHandler(this)

        if(!name.isEmpty() && !email.isEmpty()) {
            //passes the name and email to the addEmployee in our database handler -- the ID (0) will be automatically incremented.
            //even though 0 is in the argument, it will still increment
            val status = databaseHandler.addEmployee(EmpModelClass(0, name, email))
            if (status > -1) {
                Toast.makeText(applicationContext, "Record saved,", Toast.LENGTH_LONG).show()
                etName.text.clear()
                etEmailId.text.clear()

                setupListofDataIntoRecyclerView()
            }
        }else{
            Toast.makeText(applicationContext," saved", Toast.LENGTH_LONG).show()

        }
    }//11

    private fun setupListofDataIntoRecyclerView() {

        if(getItemsList().size > 0) {

            rvItemsList.visibility = View.VISIBLE
            tvNoRecordsAvailable.visibility = View.GONE


            rvItemsList.LayoutManager = LinearLayoutManager(this)

            val itemAdapter = ItemAdapter(this, getItemsList())

            rvItemsList.adapter = itemAdapter
        }else {
            rvItemsList.visibility = View.GONE
            tvNoRecordsAvailable.visibility = View.VISIBLE
        }
    }//12

    private fun getItemsList() : ArrayList<EmpModelClass> {
        val databaseHandler: DatabaseHandler = DatabaseHandler(this)

        return databaseHandler.viewEmployee()
    }//13

    //it creates the update
    fun updateRecordDialog(empModelClass: EmpModelClass) {
        val updateDialog = Dialog(this, R.style.Theme_Dialog)
        updateDialog.setCancelable(false)

        updateDialog.setContentView(R.layout.dialog_update)

        //sets the edit text dialog to whatever was previously in that record
        updateDialog.etUpdateName.setText(empModelClass.name)
        updateDialog.etUpdateEmailId.setText(empModelClass.email)

        updateDialog.tvUpdate.setOnClickListener(View.OnClickListener{
            val name = updateDialog.etUpdateName.text.toString()
            val email = updateDialog.etUpdateEmailId.text.toString()

            val databaseHandler: DatabaseHandler = DatabaseHandler(this)

            if(!name.isEmpty() && !email.isEmpty()) {
                val status = databaseHandler.updateEmployee(EmpModelClass(empModelclass.id, name, email))
                if(status>-1) {
                    Toast.makeText(applicationContext, "Record Updated.", Toast.LENGTH_LONG).show()

                    setupListofDataIntoRecyclerView()

                    updateDialog.dismiss()
                }
            }else{
                Toast.makeText(applicationContext, "Name or Email cannot be blank", Toast.LENGTH_LONG).show()
            }
        })
        updateDialog.tvCancel.setOnClickListener({
            updateDialog.dismiss()
        })
        updateDialog.show()
    }//14

    fun deleteRecordAlertDialog(empModelClass:EmpModelClass) {
        //imported AlertDialog
        val builder = AlertDialog.Builder(this)

        builder.setTitle("delete record")
        builder.setMessage("Are you sure you want to delete it?")
        builder.setIcon(android.R.drawable.ic_dialog_alert)

        builder.setPositiveButton("Yes") { dialogInterface, which ->

            val databaseHandler: DatabaseHandler = DatabaseHandler(this)
            val status = databaseHandler.deleteEmployee(EmpModelClass(empModelClass.id, "", ""))
            if(status > -1) {
                Toast.makeText(applicationContext, "Record deleted successfully", Toast.LENGTH_LONG
                ).show()

                setupListofDataIntoRecyclerView()
            }
            dialogInterface.dismiss()
        }

        val alertDialog: AlertDialog = builder.create()

        alertDialog.setCancelable(false)
        alertDialog.show()
    }//15
}