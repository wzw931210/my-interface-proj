package cn.jinjing.plat.util;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.HColumnDescriptor;
import org.apache.hadoop.hbase.HTableDescriptor;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.*;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class HbaseUtil {
    private static Log log = LogFactory.getLog(HbaseUtil.class);

    //声明静态配置
    static Configuration conf = null;
    private static final String zk = ConfigUtil.getProperties("zk_url");
    private static int POOL_SIZE = Integer.parseInt(ConfigUtil.getProperties("pool_size"));
    private static Connection conn = null;

    public static Connection getConnection()
    {
        if(conn == null || conn.isClosed())
        {
            try {
                conf = HBaseConfiguration.create();
                conf.set("hbase.zookeeper.quorum", zk);
                ExecutorService ececutor = Executors.newFixedThreadPool(POOL_SIZE);
                conn = ConnectionFactory.createConnection(conf, ececutor);
            } catch (IOException e) {
                e.printStackTrace();
                log.error(ExceptionUtil.printStackTraceToString(e));
            }
        }
        return conn;
    }

    /**
     * crate HBase table
     * @param tableName table name
     * @param family family
     */
    public static void createTable(String tableName,String family){
        Admin admin = null;
        try {
            admin = HbaseUtil.getConnection().getAdmin();
            TableName tbName = TableName.valueOf(tableName);
            if (!admin.tableExists(tbName)) {
                HTableDescriptor hTableDesc = new HTableDescriptor(tbName);
                hTableDesc.addFamily(new HColumnDescriptor(family));
                admin.createTable(hTableDesc);
            }
        }catch(IOException e){
            log.error(ExceptionUtil.printStackTraceToString(e));
            e.printStackTrace();
        }finally {
            try {
                if (admin != null) {
                    admin.close();
                }
            }catch(IOException e){
                log.error(ExceptionUtil.printStackTraceToString(e));
                e.printStackTrace();
            }
        }

    }

}
