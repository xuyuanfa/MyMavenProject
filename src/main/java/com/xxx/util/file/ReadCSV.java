package com.xxx.util.file;

import java.io.File;
import java.nio.charset.Charset;
import java.util.List;
import java.util.Map;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.apache.commons.lang3.StringUtils;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

public class ReadCSV {

	private static final String[] FILE_HEADERS = { "交易日期", "摘要", "交易场所", "交易国家或地区简称", "钞/汇", "交易金额(收入)", "交易金额(支出)", "交易币种", "记账金额(收入)", "记账金额(支出)", "记账币种",
			"余额", "对方户名" };
	private static final CSVFormat CSV_FORMAT = CSVFormat.DEFAULT.withHeader(FILE_HEADERS);

	public static void main(String[] args) throws Exception {
		File file = new File("D:/download/hisdetail1450421876529.csv");
		CSVParser csvParser = CSVParser.parse(file, Charset.forName("GBK"), CSV_FORMAT);
		List<CSVRecord> records = csvParser.getRecords();
		for (CSVRecord record : records) {
			for (String content : record) {
				System.out.print(content);
				System.out.print("  ");
			}
			System.out.println();
		}
		System.out.println(JSONObject.toJSON(records));
		System.out.println(records.get(4).get("余额"));
		JSONArray recordsJSONArray = new JSONArray();
		for(CSVRecord record : records){
			recordsJSONArray.add(JSONObject.toJSON(record.toMap()));
		}
		System.out.println(recordsJSONArray.toJSONString());
		
		recordsJSONArray = new JSONArray();
		for(int i=0; i<records.size(); i++){
			if(i < 4 || i >= records.size() - 2){
				continue;
			}
			CSVRecord record = records.get(i);
			JSONObject recordJSON = new JSONObject();
			Map<String, String> recordMap = record.toMap();
			for(Map.Entry<String, String> entry : recordMap.entrySet()){
				recordJSON.put(entry.getKey(), StringUtils.trim(entry.getValue()));
			}
			recordsJSONArray.add(recordJSON);
//			recordsJSONArray.add(JSONObject.toJSON(record.toMap()));
		}
		System.out.println(recordsJSONArray.toJSONString());
		csvParser.close();
	}
}
