/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package xmldatabase;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 *
 * @author soulq
 */
public class XMLDatabaseQuery3 {
        /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        Connection connect = null;
        try {
//            Class.forName("com.mariadb.jdbc.Driver");
            Class.forName("org.mariadb.jdbc.Driver");
            connect = DriverManager.getConnection("jdbc:mariadb://localhost/ece_item" + "?user=root&password=323643123");
            if(connect != null){
                System.out.println("Database Connected");
                System.out.println("-------------------------------------------");
                // sql query instruction
                Statement stmt = connect.createStatement();  // create Object of statement to make query
                String sql = "select take_name as name, personal.department , order_no, take_date \n" +
                    "from orders\n" +
                    "inner join personal\n" +
                    "on orders.take_name = personal.p_name\n" +
                    "where orders.order_no = (\n" +
                    "		select max(order_no) from orders\n" +
                    ");";
                ResultSet rec = stmt.executeQuery(sql);  // execute and keep in Object of ResultSet
                
                if(!rec.next())  // move cursor forward one row if have next row return true
                {   
                    System.out.println("No Records are found");  
                } else {  // if have rows
                    try{
                        DocumentBuilderFactory dbFactory =
                        DocumentBuilderFactory.newInstance();
                        DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
                        Document doc = dBuilder.newDocument();

                        // root element
                        Element rootElement = doc.createElement("personalInformation");
                        doc.appendChild(rootElement);
                                                   
                        do{
                            // item element
                            Element item = doc.createElement("personal");
                            rootElement.appendChild(item);
                            Attr attr = doc.createAttribute("name");  // add id attribute
                            attr.setValue(rec.getString("name"));  // get string from column name in ResultSet
                            item.setAttributeNode(attr);
                            
                            // item name element
                            Element item_name = doc.createElement("department");
                            item.appendChild(item_name);
                            item_name.appendChild(doc.createTextNode(rec.getString("department")));
                            
                            // item price element
                            Element item_price = doc.createElement("order_number");
                            item.appendChild(item_price);
                            item_price.appendChild(doc.createTextNode(String.valueOf(rec.getInt("order_no"))));
                            
                            // item description element
                            Element item_description = doc.createElement("take_date");
                            item.appendChild(item_description);
                            item_description.appendChild(doc.createTextNode(rec.getString("take_date")));
                            
                        }
                        while(rec.next());  // move cursor forward one row
                                              
                    // write the content into xml file
                    TransformerFactory transformerFactory = TransformerFactory.newInstance();
                    Transformer transformer = transformerFactory.newTransformer();
                    DOMSource source = new DOMSource(doc);
                    StreamResult result = new StreamResult(new File("D:\\Google Drive\\Selected Topics in "
                            + "Computer Engineering II\\xml\\database\\take_personal_information.xml"));
                    transformer.transform(source, result);

                    // Output to console for testing
                    StreamResult consoleResult = new StreamResult(System.out);
                    transformer.transform(source, consoleResult);
                    
                    } catch (Exception e){
                                e.printStackTrace();
                    }
                }
                
            } else {
                System.out.println("Database Connect Failed.");
            }
        } catch (Exception e){
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        
        // Close
        try {
            if(connect != null){
                connect.close();
            } 
        } catch (SQLException e){
            // TODO Auto-generated catch block
            e.printStackTrace();
        } 
    }
}
