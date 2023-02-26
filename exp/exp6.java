package exp;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

import entity.FD;
import entity.Key;
import entity.Parameter;
import entity.Schema;
import util.DBUtils;
import util.Utils;

/**
 * In this experiment, with given schema and FDs, we synthesize Armstrong Relation and its copies in different sizes.
 * Then, computing the optimal cover and mixed optimal cover of FDs.
 * In this two FD cover and different sizes of Armstrong Relation, do update experiments.
 *
 */
public class exp6 {
	/**
	 * 
	 * @return traffic dataset's schema
	 */
	public static Schema getTrafficSchema() {
		String cs = "CAR-SERIAL#";
		String lic = "LICENSE#";
		String owner = "OWNER";
		String date = "DATE";
		String time = "TIME";
		String tic = "TICKET#";
		String offe = "OFFENSE";
		//schema
		List<String> R = Arrays.asList(cs,lic,owner,date,time,tic,offe);
		//example 5.13 F from Maier book
		FD fd1 = new FD(Arrays.asList(cs),Arrays.asList(lic, owner));
		FD fd2 = new FD(Arrays.asList(lic),Arrays.asList(cs));
		FD fd3 = new FD(Arrays.asList(tic),Arrays.asList(lic, date, time, offe));
		FD fd4 = new FD(Arrays.asList(lic, date, time),Arrays.asList(tic, offe));
		List<FD> FDs = Arrays.asList(fd1,fd2,fd3,fd4);//original FD cover
		
		return new Schema(R,FDs);
	}
	
	/**
	 * 
	 * @return a minimal Armstrong relation of traffic data set
	 */
	public static List<List<String>> getTrafficArmRel(){
		List<String> t1 = Arrays.asList("0","0","0","0","0","0","0");
		List<String> t2 = Arrays.asList("1","1","0","0","0","1","0");
		List<String> t3 = Arrays.asList("0","0","0","1","0","2","0");
		List<String> t4 = Arrays.asList("0","0","0","0","1","3","0");
		List<String> t5 = Arrays.asList("2","2","0","0","0","4","1");
		List<String> t6 = Arrays.asList("0","0","0","2","0","5","2");
		List<String> t7 = Arrays.asList("0","0","0","0","2","6","3");
		List<String> t8 = Arrays.asList("3","3","1","0","0","7","0");
		List<String> t9 = Arrays.asList("0","0","0","3","0","8","0");
		List<String> t10 = Arrays.asList("0","0","0","0","3","9","0");
		List<String> t11 = Arrays.asList("4","4","0","0","0","10","0");
		List<String> t12 = Arrays.asList("5","5","2","4","4","11","4");
		List<List<String>> relation = Arrays.asList(t1,t2,t3,t4,t5,t6,t7,t8,t9,t10,t11,t12);
		return relation;
	}
	
	/**
	 * 
	 * @return a schema of mail data set
	 */
	public static Schema getMailSchema() {
		String add = "Address";
		String city = "City";
		String zip = "Zip";
		//R
		List<String> R = Arrays.asList(add,city,zip);
		FD fd1 = new FD(Arrays.asList(add,city), Arrays.asList(zip));
		FD fd2 = new FD(Arrays.asList(zip), Arrays.asList(city));
		List<FD> FDs = Arrays.asList(fd1, fd2);
		
		return new Schema(R, FDs);
	}
	
	/**
	 * 
	 * @return a minimal Armstrong relation of mail data set
	 */
	public static List<List<String>> getMailArmRel(){
		List<String> t1 = Arrays.asList("0","0","0");
		List<String> t2 = Arrays.asList("1","0","1");
		List<String> t3 = Arrays.asList("0","1","2");
		List<String> t4 = Arrays.asList("2","0","0");
		List<List<String>> relation = Arrays.asList(t1,t2,t3,t4);
		return relation;
	}
	
	
	
	/**
	 * due to minimal Armstrong relation, create some copies
	 * @param minArmRel 
	 * @param startCopyNum 
	 * @param endCopyNum 
	 * @return copies of Armstrong relation
	 */
	public static List<List<String>> createCopyOfMinArmRel(List<List<String>> minArmRel,int startCopyNum,int endCopyNum){
		List<List<String>> copy = new ArrayList<List<String>>();
		for(int i = startCopyNum;i < endCopyNum;i++ ) {
			for(List<String> tuple : minArmRel) {
				ArrayList<String> t = new ArrayList<String>();
				for(String value : tuple) {
					t.add(value+"_"+i);
				}
				copy.add(t);
			}
		}
		return copy;
	}
	
	public static List<List<String>> createInsertionOfMinArmRel(List<List<String>> minArmRel,int insertNum){
		List<List<String>> insert = new ArrayList<List<String>>();
		int round = 0;
		exit :
		while(true) {
			for(List<String> tuple : minArmRel) {
				ArrayList<String> t = new ArrayList<String>();
				for(String value : tuple) {
					t.add(value+"_i"+round);
				}
				insert.add(t);
				if(insert.size() >= insertNum)
					break exit;
			}
			round ++;
		}
		return insert;
	}
	
	/**
	 * run a single experiment on  insertion
	 * @param schema
	 * @param para
	 * @param FDCoverType
	 * @param repeat
	 * @param keys
	 * @param FDs
	 * @param tableName
	 * @param insert_row_num_list
	 * @return
	 * @throws SQLException
	 * @throws IOException
	 */
	public static List<Double> runSingleExp(String output, String FDCoverType, int repeat, Schema schema, String tableName,int copyNum, List<Integer> insert_num_list) throws SQLException {
		System.out.println("\n###############################\n");
		List<Double> result = new ArrayList<Double>();
		List<String> uniqueIDs = new ArrayList<>();
		String triggerID = "tri_"+tableName;
		
		//create projection table on database
		DBUtils.createTable(tableName,schema.getAttr_set());
				
		//get Armstrong relation copy
		List<List<String>> armRel = null;
		if(tableName.equals("Traffic"))
			armRel = getTrafficArmRel();
		if(tableName.equals("Mail"))
			armRel = getMailArmRel();
		List<List<String>> armRelCopy = createCopyOfMinArmRel(armRel, 0, copyNum);
				
		//insert copy
		DBUtils.insertData(tableName,armRelCopy);
		
		//add unique constraints
		System.out.println("add trigger and unique constraints...");
		int uc_id = 0;
		for(Key k : schema.getMin_key_list()) {
			String uc_name = "uc_"+tableName+"_"+uc_id ++;
			uniqueIDs.add(uc_name);
			DBUtils.addUnique(k,tableName,uc_name);
		}
		
		//add FD triggers if exists
		if(!schema.getFd_set().isEmpty()) {
			DBUtils.addTrigger(schema.getFd_set(),tableName,triggerID);
		}
		
		
		//update experiment
		for(Integer insert_row_num : insert_num_list) {
//			int row_num = (int)(ratio*armRelCopy.size());//insert row number
			List<Double> cost_list = new ArrayList<Double>();
			List<List<String>> syntheData = createInsertionOfMinArmRel(armRel, insert_row_num);
			for(int i = 0;i < repeat;i ++) {
				double cost = DBUtils.insertData(tableName,syntheData);
				cost_list.add(cost);
				DBUtils.deleteData(tableName,"`id` > "+armRelCopy.size());
			}
			result.add(Utils.getAve(cost_list));
			result.add(Utils.getMedian(cost_list));
		}
		
		//drop the table
		DBUtils.dropTable(tableName);
		
		//output
		String stat = "";
		for(double a : result) {
			stat += ","+a;
		}
		String res = tableName+","+FDCoverType+","+copyNum+","+schema.getMin_key_list().size()+","+Utils.compKeyAttrSymbNum(schema.getMin_key_list())+","+schema.getFd_set().size()+","+Utils.compFDAttrSymbNum(schema.getFd_set())+stat;
		Utils.writeContent(Arrays.asList(res), output, true);
		
		System.out.println(res.replace(",", " | "));
		System.out.println("###############################\n");
		
		return result;
	}
	
	public static void runExps(String output, String tableName, int repeat,List<Integer> copy_num_list, List<Integer> insert_num_list) throws SQLException {
		List<String> FDCoverTypeList = Arrays.asList("optimal");
		List<String> KeyFDCoverTypeList = Arrays.asList("optimal keyfd");
		Schema schema = null;
		if(tableName.equals("Traffic"))
			schema = getTrafficSchema();
		if(tableName.equals("Mail"))
			schema = getMailSchema();
		List<FD> FDs = schema.getFd_set();
		List<String> R = schema.getAttr_set();
		
		List<List<FD>> fdcover_list = new ArrayList<>();
		//optimal cover experiments
		for(String FDcover : FDCoverTypeList) {
			List<FD> sigma = Utils.compFDCover(FDs, FDcover);
			Schema newSchema = new Schema(R,sigma);
			fdcover_list.add(sigma);
			for(int copyNum : copy_num_list) {
				runSingleExp(output, FDcover, repeat, newSchema, tableName, copyNum, insert_num_list);
			}
		}
		//optimal mixed cover experiments
		for(int i = 0;i < fdcover_list.size();i ++) {
			String KeyFDCover = KeyFDCoverTypeList.get(i);
			List<FD> fd_cover = fdcover_list.get(i);
			List<Object> keyfdcover = Utils.compKeyFDCover(R, fd_cover);
			List<Key> sigma_k = (List<Key>) keyfdcover.get(0);
			List<FD> sigma_f = (List<FD>) keyfdcover.get(1);
			Schema newSchema = new Schema(R, sigma_f, sigma_k);
			for(int copyNum : copy_num_list) {
				runSingleExp(output, KeyFDCover, repeat, newSchema, tableName, copyNum, insert_num_list);
			}
		}
		
	}
	
	public static void main(String[] args) throws SQLException {
		String output = "C:\\Users\\freem\\Desktop\\PhD\\FDCover的相关工作\\FDCover实验\\Exp Results\\results.csv";
		String tableName = "Traffic";
		int repeat = 3;
		List<Integer> insert_copy_list = Arrays.asList(100,500,2500,12500);
		List<Integer> insert_row_list = Arrays.asList(1000,2000,3000,4000);
		exp6.runExps(output, tableName, repeat, insert_copy_list, insert_row_list);
		
	}

}
