package hbaseTest;

import java.io.IOException;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.*;
import org.apache.hadoop.hbase.client.*;
import org.apache.hadoop.hbase.io.compress.Compression.Algorithm;
import org.apache.hadoop.hbase.util.Bytes;

public class HbaseTable
{

	private static final String TABLE_NAME = "user";

	public static void main(String... args) throws IOException
	{

		Configuration config = HBaseConfiguration.create();

		try (Connection connection = ConnectionFactory.createConnection(config);
                Admin admin = connection.getAdmin();
                Table table1 = connection.getTable(TableName.valueOf(TABLE_NAME))
        ) {
			HTableDescriptor table = new HTableDescriptor(TableName.valueOf(TABLE_NAME));
			table.addFamily(new HColumnDescriptor("Row Key").setCompressionType(Algorithm.NONE));
			table.addFamily(new HColumnDescriptor("Personal Data"));
			table.addFamily(new HColumnDescriptor("Professional Data"));

            System.out.print("Creating table... ");

			if (admin.tableExists(table.getTableName()))
			{
				admin.disableTable(table.getTableName());
				admin.deleteTable(table.getTableName());
			}
			admin.createTable(table);

            System.out.print("Inserting data... ");

            table1.put(getPut("row1", "1", "John", "Boston", "Manager", "150,000"));
            table1.put(getPut("row2", "2", "Mary", "New York", "Sr. Engineer", "130,000"));
            table1.put(getPut("row3", "3", "Bob", "Fremont", "Jr. Engineer", "90,000"));
            System.out.println("data inserted successfully");

            System.out.print("Updating some data... ");
            debug(table1);

            table1.put(getPut("row3", "3", "Bob", "Fremont", "Sr. Engineer", Double.toString(90000*1.05)));
            debug(table1);

			System.out.println(" Done!");
		} catch (Exception e) {
            System.out.println(e.getStackTrace());
        }
	}

    private static Put getPut(String row, String empId, String name, String city, String des, String sal) {
        Put p = new Put(Bytes.toBytes(row));

        p.addColumn(Bytes.toBytes("Row Key"), Bytes.toBytes("Empid"), Bytes.toBytes(empId));
        p.addColumn(Bytes.toBytes("Personal Data"), Bytes.toBytes("Name"), Bytes.toBytes(name));
        p.addColumn(Bytes.toBytes("Personal Data"), Bytes.toBytes("City"), Bytes.toBytes(city));
        p.addColumn(Bytes.toBytes("Professional Data"), Bytes.toBytes("Designation"), Bytes.toBytes(des));
        p.addColumn(Bytes.toBytes("Professional Data"), Bytes.toBytes("salary"), Bytes.toBytes(sal));

        return p;
    }

    private static void debug(Table table1) throws IOException {
        Get g = new Get(Bytes.toBytes("row3"));
        Result r = table1.get(g);
        byte [] value = r.getValue(Bytes.toBytes("Professional Data"),Bytes.toBytes("Designation"));
        byte [] value1 = r.getValue(Bytes.toBytes("Professional Data"),Bytes.toBytes("salary"));
        String des = Bytes.toString(value);
        String sal = Bytes.toString(value1);
        System.out.println(des + " " + sal);
    }
}
