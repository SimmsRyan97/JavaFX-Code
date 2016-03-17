
package zderby;

import java.net.URL;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Collections;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;


public class ZDerbyController implements Initializable {

@FXML private TableView<UserData> table;// NOTE CONSTRUCTION see Observable Value
@FXML private TableColumn<UserData,String> IDCol;
@FXML private TableColumn<UserData,String> firstNameCol;
@FXML private TableColumn<UserData,String> lastNameCol;
@FXML private TableColumn<UserData,String> phoneNumCol;
@FXML private TableColumn<UserData,String> notesCol;
@FXML private TextField txfID,txfFName,txfLName,txfAC,txfPF,txfSF,txfNotes,txfInfo;  
@FXML private Button btnEdit,btnDelete,btnSave,btnAdd,btnCancel;

String dbName="InfoDB";
String conURL = "jdbc:derby:C:/A_DerbyDataBase/DBName" + dbName + ";create=true";
// Line of Code Above creates a folder A_DerbyDataBase on C Drive with a 
// Sub-Folder named DBNameInfoDB these folders are NOT Removed with the unins000.exe
int rec;
Connection con = null;
Statement stmnt = null;
PreparedStatement pstmt = null;


    // This SQL creates the table "infodata" with the column names
    // ID - lname - fname - phone - notes & makes ID Column the primary key
    String SQL_CreateTable = "create table infodata ("
    + "ID     int not null generated always as identity "
    + "       (start with 1000), "
    + "fname  varchar(40) not null, lname varchar(40) not null, "
    + "phone  varchar(14) not null, notes varchar(256) not null, "
    + "primary key (ID) )";
    
    // Cancel ADD DATA or SAVE EDIT or DELETE
    @FXML
    private void onCancel(ActionEvent e) throws SQLException{

    if(btnEdit.isVisible()){
        ReadFromDB();
        ClearTXF();
        btnEdit.setVisible(false);
        btnDelete.setVisible(false);
        btnCancel.setVisible(false);
        btnAdd.setVisible(true);
        txfInfo.setText("Select a Record from the TABLE or ADD DATA");
    }else{
        btnCancel.setVisible(false);
        btnAdd.setVisible(true);
        btnSave.setVisible(false);
        txfInfo.setText("Select a Record from the TABLE or ADD DATA");
    }
        MakeUNEditable();
    }
        
    @FXML // Setup For Adding A New Record
    private void onAdd(ActionEvent e) throws SQLException{
        btnCancel.setVisible(true);
        ClearTXF();
        MakeEditable();
        txfFName.requestFocus();
        btnSave.setVisible(true);
        btnAdd.setVisible(false);
        btnEdit.setVisible(false);
        btnDelete.setVisible(false);
        txfInfo.setText("Add New Record");
        //DatabaseMetaData dbmd = con.getMetaData();// For Development
        //String dbURL = dbmd.getURL();
        //System.out.println("Database URL:" + dbURL);
    }    
        
    @FXML// Save Data from TextFields with PrepareStatement
    private void onSave(ActionEvent e)throws SQLException{
        
        // Add Error Trapping Here with Alert Dialog
        //if(txfAC.getText().equals("")){
            //txfSF.requestFocus();
            //return;
        //}
   
        String sql = "INSERT INTO infodata (fname,lname,phone,notes)values(?,?,?,?)";
        pstmt = con.prepareStatement(sql); 
        pstmt.setString(1, txfFName.getText());
        pstmt.setString(2, txfLName.getText());
            
    if(txfAC.getText().equals("")&& txfPF.getText().equals("")&& txfSF.getText().equals("")){
        pstmt.setString(3, "");
    }else{
        pstmt.setString(3, txfAC.getText()+ "-" + txfPF.getText()+ "-" + txfSF.getText());
    }
        pstmt.setString(4, txfNotes.getText());
        pstmt.executeUpdate();
        pstmt.close();
        ReadFromDB();
        txfInfo.setText("Record Inserted");
        btnSave.setVisible(false);
        btnAdd.setVisible(true);
        btnCancel.setVisible(false);
        MakeUNEditable();
        ClearTXF();
    }
    
    // This Saves NEW Data to infodata with the same ID number EDIT
    @FXML
    private void onEdit(ActionEvent e) throws SQLException{
        
        if(txfID.getText().equals("")){
            return;// Code not needed as txfID is not Editable
        }
        
        String sql = "UPDATE infodata SET fname = ?,lname = ?,phone = ?,notes = ? WHERE ID = ?";
        pstmt = con.prepareStatement(sql); 
        pstmt.setString(1, txfFName.getText());
        pstmt.setString(2, txfLName.getText());
        
    if(txfAC.getText().equals("")&& txfPF.getText().equals("")&& txfSF.getText().equals("")){
        pstmt.setString(3, "");
    }else{
        pstmt.setString(3, txfAC.getText()+ "-" + txfPF.getText()+ "-" + txfSF.getText());
    }
        pstmt.setString(4, txfNotes.getText());
        
        int ID = Integer.valueOf(txfID.getText());
        pstmt.setInt(5, ID);
        
        pstmt.executeUpdate();
        pstmt.close();
        ReadFromDB();
        btnEdit.setVisible(false);
        btnDelete.setVisible(false);
        btnCancel.setVisible(false);
        btnAdd.setVisible(true);
        txfInfo.setText("Record Edited");
        MakeUNEditable();
    }
    
    // Delete a Record
    @FXML
    private void onDelete(ActionEvent e) throws SQLException{
        
        if(txfID.getText().equals("")){// Test no ID value no Recorod Deleted
            return;// Code not needed 
        }
            
        String sql = "DELETE FROM infodata WHERE ID = ?";
        pstmt = con.prepareStatement(sql); 
        int ID = Integer.valueOf(txfID.getText());
        pstmt.setInt(1, ID);
        pstmt.executeUpdate();
        pstmt.close();

        ReadFromDB();
        btnEdit.setVisible(false);
        btnDelete.setVisible(false);
        btnCancel.setVisible(false);
        btnAdd.setVisible(true);
        txfInfo.setText("Record Deleted");
    }

    private void MakeConn() throws SQLException{
        //Driver derbyEmbeddedDriver = new org.apache.derby.jdbc.EmbeddedDriver();
        //DriverManager.registerDriver(derbyEmbeddedDriver);
        con = DriverManager.getConnection(conURL);
        //System.out.println("Connected to "+dbName);
        MakeTable();
    }
    
    private void MakeTable() throws SQLException{
    
    if (!tableExists( con, "infodata")){
        txfInfo.setText("Creating Table info");
        stmnt = con.createStatement();
        stmnt.execute(SQL_CreateTable );
        stmnt.close();
    }else{
        txfInfo.setText("Table Already Created ");
        ReadFromDB();// READ data into TableView
    }
    }
    
    // Does the table EXISTS 
    private static boolean tableExists ( Connection con, String table ) {
        int numRows = 0;
        try {
            DatabaseMetaData dbmd = con.getMetaData();
            // Note the args to getTables are case-sensitive!
            ResultSet rs = dbmd.getTables( null, "APP", table.toUpperCase(), null);
        while( rs.next()) ++numRows;
        
        }catch(SQLException e){
            String theError = e.getSQLState();
            System.out.println("Can't query DB metadata: " + theError );
            System.exit(1);
        }
            return numRows > 0;
    }
    
    private void ReadFromDB() throws SQLException{
        stmnt = con.createStatement();
        ObservableList<UserData> TableData = FXCollections.observableArrayList();
        ResultSet rs = stmnt.executeQuery("SELECT * FROM infodata");// Get all DB data
    int rowCount = 0;

        while (rs.next()){// Add data to observableArrayList TableData
            rowCount++;
            TableData.add(new UserData(rs.getString("ID"),rs.getString("fname")
            ,rs.getString("lname"),rs.getString("phone"),rs.getString("notes")));
        }
        
    System.out.println("Row Count "+rowCount);// Useful for Printing for further development
    
        PropertyValueFactory<UserData, String> IDCellValueFactory = new PropertyValueFactory<>("ID");
        IDCol.setCellValueFactory(IDCellValueFactory);
        PropertyValueFactory<UserData, String> FirstNameCellValueFactory = new PropertyValueFactory<>("fname");
        firstNameCol.setCellValueFactory(FirstNameCellValueFactory);
        PropertyValueFactory<UserData, String> LastNameCellValueFactory = new PropertyValueFactory<>("lname");
        lastNameCol.setCellValueFactory(LastNameCellValueFactory);
        PropertyValueFactory<UserData, String> PhoneNumCellValueFactory = new PropertyValueFactory<>("phone");
        phoneNumCol.setCellValueFactory(PhoneNumCellValueFactory);
        PropertyValueFactory<UserData, String> NotesCellValueFactory = new PropertyValueFactory<>("notes");
        notesCol.setCellValueFactory(NotesCellValueFactory);

        Collections.sort(TableData, (p1, p2)-> p1.getLastName().compareToIgnoreCase(p2.getLastName()));
        // Line of Code above Sorts lastNameCol alpha 
        
        if(TableData.size()<6) {// Format TableView to display Vertical ScrollBar
                table.setPrefWidth(383);
        }else {
                table.setPrefWidth(400);
        }
        
        table.setItems(TableData);
        stmnt.close();
        rs.close();
    }
    
    // Model Class added to Controller
    public static class UserData {
            
        private final StringProperty ID;
        private final StringProperty fname;
        private final StringProperty lname;
        private final StringProperty phone;
        private final StringProperty notes;

        public UserData(String ID,String fname,String lname,String phone,String notes) {
            this.ID = new SimpleStringProperty(ID);
            this.fname = new SimpleStringProperty(fname);
            this.lname = new SimpleStringProperty(lname);
            this.phone = new SimpleStringProperty(phone);
            this.notes = new SimpleStringProperty(notes);
        }
    
        public String getID() {// 0
            return ID.get();
        }
        public void setID(String ID){// 0
            this.ID.set(ID);
        }
        public StringProperty IDProperty(){// 0
            return ID;
        }
        
        public String getFirstName() {// 1
            return fname.get();
        }
        public void setfname(String fname){// 1
            this.fname.set(fname);
        }
        public StringProperty fnameProperty(){// 1
            return fname;
        }
        
        public String getLastName() {// 2
            return lname.get();
        }
        public void setlname(String lname){// 2
            this.lname.set(lname);
        }
        public StringProperty lnameProperty(){// 2
            return lname;
        }
        
        public String getPhone() {// 3
            return phone.get();
        }
        public void setphone(String phone){// 3
            this.phone.set(phone);
        }
        public StringProperty phoneProperty(){// 3
            return phone;
        }
        
        public String getNotes() {// 4
            return notes.get();
        }
        public void setnotes(String notes){// 4
            this.notes.set(notes);
        }
        public StringProperty notesProperty(){// 4
            return notes;
        }
    }
    
    private void ClearTXF(){
        txfID.setText("");
        txfFName.setText("");
        txfLName.setText("");
        txfAC.setText("");
        txfPF.setText("");
        txfSF.setText("");
        txfNotes.setText("");
    }
    
    private void MakeUNEditable(){
        txfFName.setEditable(false);
        txfLName.setEditable(false);
        txfAC.setEditable(false);
        txfPF.setEditable(false);
        txfSF.setEditable(false);
        txfNotes.setEditable(false);   
    }
    
    private void MakeEditable(){
        txfFName.setEditable(true);
        txfLName.setEditable(true);
        txfAC.setEditable(true);
        txfPF.setEditable(true);
        txfSF.setEditable(true);
        txfNotes.setEditable(true);
    }
    
    // This method displays data in the TextFields when a row is selected in the TableView
    private void showTableDataDetails(UserData info){
       
    if(btnSave.isVisible()){
        btnSave.setVisible(false);
    }
        ClearTXF();
        btnEdit.setVisible(true);
        btnDelete.setVisible(true);
        btnCancel.setVisible(true);
        txfInfo.setText("Record can be Edited or Deleted");
        MakeEditable();
          
    if (info != null) {
        info =  (UserData) table.getSelectionModel().getSelectedItem(); 
        txfID.setText(info.getID());
        txfFName.setText(info.getFirstName());
        txfLName.setText(info.getLastName());

        String gp = info.getPhone();// Test for no data in Phone Col of DB
        if(!gp.equals("")){
        txfAC.setText(info.getPhone().substring(0, 3));
        txfPF.setText(info.getPhone().substring(4, 7));
        txfSF.setText(info.getPhone().substring(8, 12));
        }

        txfNotes.setText(info.getNotes());
        txfFName.requestFocus();
    }
    }
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {

        table.getSelectionModel().selectedItemProperty().addListener((ObservableValue<? extends UserData>
            observable,UserData oldValue, UserData newValue) -> {
            showTableDataDetails((UserData) newValue); // When a row of the table is Selected call 
            // Proper Construction                     // showTableDataDetails method  
        });
        
        try {
            MakeConn();
        } catch (SQLException ex) {
            Logger.getLogger(ZDerbyController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }     
}
