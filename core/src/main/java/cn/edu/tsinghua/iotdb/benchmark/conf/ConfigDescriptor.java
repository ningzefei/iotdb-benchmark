package cn.edu.tsinghua.iotdb.benchmark.conf;

import cn.edu.tsinghua.iotdb.benchmark.workload.reader.DataSet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.Arrays;
import java.util.Properties;

public class ConfigDescriptor {
	private static final Logger LOGGER = LoggerFactory.getLogger(ConfigDescriptor.class);

	private final Config config;

	private static class ConfigDescriptorHolder {
		private static final ConfigDescriptor INSTANCE = new ConfigDescriptor();
	}

	private ConfigDescriptor() {
		config = new Config();
		loadProps();
		config.initInnerFunction();
		config.initDeviceCodes();
		config.initSensorCodes();
		config.initSensorFunction();
		config.initRealDataSetSchema();
	}

	public static ConfigDescriptor getInstance() {
		return ConfigDescriptorHolder.INSTANCE;
	}

	public Config getConfig(){
		return config;
	}

	private void loadProps() {
		String url = System.getProperty(Constants.BENCHMARK_CONF, "conf/config.properties");
		if (url != null) {
			InputStream inputStream;
			try {
				inputStream = new FileInputStream(new File(url));
			} catch (FileNotFoundException e) {
				LOGGER.warn("Fail to find config file {}", url);
				return;
			}
			Properties properties = new Properties();
			try {
				properties.load(inputStream);
				String hosts = properties.getProperty("HOST", "no HOST");
				config.setHOST(Arrays.asList(hosts.split(",")));
				String ports = properties.getProperty("PORT", "no PORT");
				config.setPORT(Arrays.asList(ports.split(",")));
				config.setENABLE_DOUBLE_INSERT(Boolean.parseBoolean(
						properties.getProperty("ENABLE_DOUBLE_INSERT", config.isENABLE_DOUBLE_INSERT() + "")));
				String another_host = properties.getProperty("ANOTHER_HOST", "no ANOTHER_HOST");
				config.setANOTHER_HOST(Arrays.asList(another_host.split(",")));
				String another_port = properties.getProperty("ANOTHER_PORT", "no ANOTHER_PORT");
				config.setANOTHER_PORT(Arrays.asList(another_port.split(",")));
				config.setANOTHER_DB_NAME(properties.getProperty("ANOTHER_DB_NAME", "no ANOTHER_DB_NAME"));
				config.setKAFKA_LOCATION(properties.getProperty("KAFKA_LOCATION", "no KAFKA_LOCATION"));
				config.setZOOKEEPER_LOCATION(properties.getProperty("ZOOKEEPER_LOCATION", "no ZOOKEEPER_LOCATION"));
				config.setTOPIC_NAME(properties.getProperty("TOPIC_NAME", "NULL"));

				config.setIOTDB_SESSION_POOL_SIZE(Integer.parseInt(properties.getProperty("IOTDB_SESSION_POOL_SIZE", config.getIOTDB_SESSION_POOL_SIZE() + "")));

				config.setDEVICE_NUMBER(Integer.parseInt(properties.getProperty("DEVICE_NUMBER", config.getDEVICE_NUMBER()+"")));
				config.setSENSOR_NUMBER(Integer.parseInt(properties.getProperty("SENSOR_NUMBER", config.getSENSOR_NUMBER()+"")));
				config.setIS_SENSOR_TS_ALIGNMENT(Boolean.parseBoolean(properties.getProperty("SENSOR_TS_ALIGNMENT", config.isIS_SENSOR_TS_ALIGNMENT()+"")));
				config.setFILE_PATH(properties.getProperty("FILE_PATH", "no file"));

				String dataset = properties.getProperty("DATA_SET", "NULL");
				switch (properties.getProperty("DATA_SET", "REDD")) {
					case "GEOLIFE": config.setDATA_SET(DataSet.GEOLIFE); break;
					case "REDD": config.setDATA_SET(DataSet.REDD); break;
					case "TDRIVE": config.setDATA_SET(DataSet.TDRIVE); break;
					case "NOAA": config.setDATA_SET(DataSet.NOAA); break;
					default: throw new RuntimeException("not support dataset: " + dataset);
				}

				config.setPOINT_STEP(Long.parseLong(properties.getProperty("POINT_STEP", config.getPOINT_STEP()+"")));
				config.setBATCH_SIZE_PER_WRITE(Integer.parseInt(properties.getProperty("BATCH_SIZE_PER_WRITE", config.getBATCH_SIZE_PER_WRITE() + "")));
				config.setSG_STRATEGY(properties.getProperty("SG_STRATEGY", "hash"));
				config.setLOOP(Long.parseLong(properties.getProperty("LOOP", config.getLOOP()+"")));
				config.setLINE_RATIO(Double.parseDouble(properties.getProperty("LINE_RATIO", config.getLINE_RATIO()+"")));
				config.setSIN_RATIO(Double.parseDouble(properties.getProperty("SIN_RATIO", config.getSIN_RATIO()+"")));
				config.setSQUARE_RATIO(Double.parseDouble(properties.getProperty("SQUARE_RATIO", config.getSQUARE_RATIO()+"")));
				config.setRANDOM_RATIO(Double.parseDouble(properties.getProperty("RANDOM_RATIO", config.getRANDOM_RATIO()+"")));
				config.setCONSTANT_RATIO(Double.parseDouble(properties.getProperty("CONSTANT_RATIO", config.getCONSTANT_RATIO()+"")));

				config.setINTERVAL(Integer.parseInt(properties.getProperty("INTERVAL", config.getINTERVAL()+"")));
				config.setCLIENT_NUMBER(Integer.parseInt(properties.getProperty("CLIENT_NUMBER", config.getCLIENT_NUMBER()+"")));
				config.setGROUP_NUMBER(Integer.parseInt(properties.getProperty("GROUP_NUMBER", config.getGROUP_NUMBER()+"")));

				config.setDB_NAME(properties.getProperty("DB_NAME", "test"));
				config.setDB_SWITCH(properties.getProperty("DB_SWITCH", Constants.DB_IOT));

				config.setTIMESTAMP_PRECISION(properties.getProperty("TIMESTAMP_PRECISION", config.getTIMESTAMP_PRECISION()+""));
				switch (config.getTIMESTAMP_PRECISION()) {
					case "ms":  break;
					case "us":
						if (!config.getDB_SWITCH().contains("IoTDB") && !config.getDB_SWITCH().equals("InfluxDB")){
							throw new RuntimeException("The database " + config.getDB_SWITCH() + " can't use microsecond precision");
						}
						break;
					default: throw new RuntimeException("not support timestamp precision: " + config.getTIMESTAMP_PRECISION());
				}

				config.setQUERY_SENSOR_NUM(Integer.parseInt(properties.getProperty("QUERY_SENSOR_NUM", config.getQUERY_SENSOR_NUM()+"")));
				config.setQUERY_DEVICE_NUM(Integer.parseInt(properties.getProperty("QUERY_DEVICE_NUM", config.getQUERY_DEVICE_NUM()+"")));
				config.setQUERY_AGGREGATE_FUN(properties.getProperty("QUERY_AGGREGATE_FUN", config.getQUERY_AGGREGATE_FUN()));
				config.setQUERY_INTERVAL(Long.parseLong(properties.getProperty("QUERY_INTERVAL", config.getQUERY_INTERVAL()+"")));
				config.setWRITE_OPERATION_TIMEOUT_MS(Integer.parseInt(properties
						.getProperty("WRITE_OPERATION_TIMEOUT_MS", config.getWRITE_OPERATION_TIMEOUT_MS() + "")));
				config.setREAD_OPERATION_TIMEOUT_MS(Integer.parseInt(properties
						.getProperty("READ_OPERATION_TIMEOUT_MS", config.getREAD_OPERATION_TIMEOUT_MS() + "")));
				config.setQUERY_LOWER_VALUE(Double.parseDouble(properties.getProperty("QUERY_LOWER_VALUE", config.getQUERY_LOWER_VALUE() + "")));
				config.setQUERY_SEED(Long.parseLong(properties.getProperty("QUERY_SEED", config.getQUERY_SEED()+"")));
				config.setREMARK(properties.getProperty("REMARK", "-"));
				config.setMYSQL_REAL_INSERT_RATE(Double.parseDouble(properties.getProperty("MYSQL_REAL_INSERT_RATE", config.getMYSQL_REAL_INSERT_RATE()+ "")));
				config.setTEST_DATA_STORE_PORT(properties.getProperty("TEST_DATA_STORE_PORT", config.getTEST_DATA_STORE_PORT()));
				config.setTEST_DATA_STORE_DB(properties.getProperty("TEST_DATA_STORE_DB", config.getTEST_DATA_STORE_DB()));
				config.setTEST_DATA_STORE_IP(properties.getProperty("TEST_DATA_STORE_IP", config.getTEST_DATA_STORE_IP()));
				config.setTEST_DATA_STORE_USER(properties.getProperty("TEST_DATA_STORE_USER", config.getTEST_DATA_STORE_USER()));
				config.setTEST_DATA_STORE_PW(properties.getProperty("TEST_DATA_STORE_PW", config.getTEST_DATA_STORE_PW()));
				config.setGROUP_BY_TIME_UNIT(Long.parseLong(properties.getProperty("GROUP_BY_TIME_UNIT", config.getGROUP_BY_TIME_UNIT() + "")));
				config.setMAX_CSV_LINE(Long.parseLong(properties.getProperty("CSV_MAX_LINE", config.getMAX_CSV_LINE()+ "")));
				config.setCSV_FILE_SPLIT(Boolean.parseBoolean(properties.getProperty("CSV_FILE_SPLIT", config.isCSV_FILE_SPLIT() + "")));

				String dataDir = properties.getProperty("IOTDB_DATA_DIR", "/home/liurui/data/data");
				config.setIOTDB_DATA_DIR(Arrays.asList(dataDir.split(",")));
				String walDir = properties.getProperty("IOTDB_WAL_DIR", "/home/liurui/data/wal");
				config.setIOTDB_WAL_DIR(Arrays.asList(walDir.split(",")));
				String systemDir = properties.getProperty("IOTDB_SYSTEM_DIR", "/home/liurui/data/system");
				config.setIOTDB_SYSTEM_DIR(Arrays.asList(systemDir.split(",")));
				for (String data_ : config.getIOTDB_DATA_DIR()) {
					config.getSEQUENCE_DIR().add(data_ + "/sequence");
					config.getUNSEQUENCE_DIR().add(data_ + "/unsequence");
				}
				config.setENCODING(properties.getProperty("ENCODING", "PLAIN"));
				config.setTEST_DATA_PERSISTENCE(properties.getProperty("TEST_DATA_PERSISTENCE", "None"));
				config.setCSV_OUTPUT(Boolean.parseBoolean(properties.getProperty("CSV_OUTPUT", config.isCSV_OUTPUT()+"")));
				config.setSTRING_LENGTH(Integer.parseInt(properties.getProperty("STRING_LENGTH", config.getSTRING_LENGTH()+"")));
				config.setLOG_PRINT_INTERVAL(Integer.parseInt(properties.getProperty("LOG_PRINT_INTERVAL", config.getLOG_PRINT_INTERVAL()+"")));
				config.setIS_QUIET_MODE(Boolean.parseBoolean(properties.getProperty("IS_QUIET_MODE", config.isIS_QUIET_MODE()+"")));
				config.setNET_DEVICE(properties.getProperty("NET_DEVICE", "e"));
				config.setWORKLOAD_BUFFER_SIZE(Integer.parseInt(properties.getProperty("WORKLOAD_BUFFER_SIZE", config.getWORKLOAD_BUFFER_SIZE()+"")));
				config.setIS_OUT_OF_ORDER(Boolean.parseBoolean(properties.getProperty("IS_OUT_OF_ORDER", config.isIS_OUT_OF_ORDER() + "")));
				config.setOUT_OF_ORDER_MODE(Integer.parseInt(properties.getProperty("OUT_OF_ORDER_MODE", config.getOUT_OF_ORDER_MODE() + "")));
				config.setOUT_OF_ORDER_RATIO(Double.parseDouble(properties.getProperty("OUT_OF_ORDER_RATIO", config.getOUT_OF_ORDER_RATIO() + "")));

				config.setBENCHMARK_WORK_MODE(properties.getProperty("BENCHMARK_WORK_MODE", ""));
				config.setMAX_K(Integer.parseInt(properties.getProperty("MAX_K", config.getMAX_K()+"")));
				config.setLAMBDA(Double.parseDouble(properties.getProperty("LAMBDA", config.getLAMBDA()+"")));
				config.setIS_REGULAR_FREQUENCY(Boolean.parseBoolean(properties.getProperty("IS_REGULAR_FREQUENCY", config.isIS_REGULAR_FREQUENCY() + "")));
				config.setCLIENT_MAX_WRT_RATE(Double.parseDouble(properties.getProperty("CLIENT_MAX_WRT_RATE", config.getCLIENT_MAX_WRT_RATE()+"")));
				config.setQUERY_LIMIT_N(Integer.parseInt(properties.getProperty("QUERY_LIMIT_N", config.getQUERY_LIMIT_N()+"")));
				config.setQUERY_LIMIT_OFFSET(Integer.parseInt(properties.getProperty("QUERY_LIMIT_OFFSET", config.getQUERY_LIMIT_OFFSET()+"")));
				config.setQUERY_SLIMIT_N(Integer.parseInt(properties.getProperty("QUERY_SLIMIT_N", config.getQUERY_SLIMIT_N()+"")));
				config.setQUERY_SLIMIT_OFFSET(Integer.parseInt(properties.getProperty("QUERY_SLIMIT_OFFSET", config.getQUERY_SLIMIT_OFFSET()+"")));
				config.setCREATE_SCHEMA(Boolean.parseBoolean(properties.getProperty("CREATE_SCHEMA", config.isCREATE_SCHEMA()+"")));
				//data type is removed in master
				//config.DATA_TYPE = properties.getProperty("DATA_TYPE", "FLOAT");
				config.setCOMPRESSOR(properties.getProperty("COMPRESSOR", "UNCOMPRESSOR"));
				config.setOPERATION_PROPORTION(properties.getProperty("OPERATION_PROPORTION", config.getOPERATION_PROPORTION()));
				config.setINSERT_DATATYPE_PROPORTION(properties.getProperty("INSERT_DATATYPE_PROPORTION", config.getINSERT_DATATYPE_PROPORTION()));
				config.setENCODING_BOOLEAN(properties.getProperty("ENCODING_BOOLEAN", config.getENCODING_BOOLEAN()));
				config.setENCODING_INT32(properties.getProperty("ENCODING_INT32", config.getENCODING_INT32()));
				config.setENCODING_INT64(properties.getProperty("ENCODING_INT64", config.getENCODING_INT64()));
				config.setENCODING_FLOAT(properties.getProperty("ENCODING_FLOAT", config.getENCODING_FLOAT()));
				config.setENCODING_DOUBLE(properties.getProperty("ENCODING_DOUBLE", config.getENCODING_DOUBLE()));
				config.setENCODING_TEXT(properties.getProperty("ENCODING_TEXT", config.getENCODING_TEXT()));
				config.setSTART_TIME(properties.getProperty("START_TIME", config.getSTART_TIME()));
				config.setINIT_WAIT_TIME(Long.parseLong(properties.getProperty("INIT_WAIT_TIME", config.getINIT_WAIT_TIME()+"")));
				config.setDATA_SEED(Long.parseLong(properties.getProperty("DATA_SEED", config.getDATA_SEED()+"")));
				config.setSTEP_SIZE(Integer.parseInt(properties.getProperty("STEP_SIZE", config.getSTEP_SIZE()+"")));
				config.setOP_INTERVAL(Integer.parseInt(properties.getProperty("OP_INTERVAL", config.getOP_INTERVAL()+"")));
				config.setIS_CLIENT_BIND(Boolean.parseBoolean(properties.getProperty("IS_CLIENT_BIND", config.isIS_CLIENT_BIND()+"")));
				config.setIS_SENSOR_TS_ALIGNMENT(Boolean.parseBoolean(properties.getProperty("IS_SENSOR_TS_ALIGNMENT", config.isIS_SENSOR_TS_ALIGNMENT()+"")));
				config.setIS_DELETE_DATA(Boolean.parseBoolean(properties.getProperty("IS_DELETE_DATA", config.isIS_DELETE_DATA()+"")));
				config.setREAL_DATASET_QUERY_START_TIME(Long.parseLong(properties.getProperty("REAL_DATASET_QUERY_START_TIME", config.getREAL_DATASET_QUERY_START_TIME() + "")));
				config.setREAL_DATASET_QUERY_STOP_TIME(Long.parseLong(properties.getProperty("REAL_DATASET_QUERY_STOP_TIME", config.getREAL_DATASET_QUERY_STOP_TIME() + "")));
				config.setBENCHMARK_CLUSTER(Boolean.parseBoolean(properties.getProperty("USE_CLUSTER",config.isBENCHMARK_CLUSTER() + "")));
				config.setENABLE_THRIFT_COMPRESSION(Boolean.parseBoolean(properties.getProperty("ENABLE_THRIFT_COMPRESSION", config.isENABLE_THRIFT_COMPRESSION() + "")));
				if (config.isBENCHMARK_CLUSTER()){
					config.setBENCHMARK_INDEX(Integer.parseInt(properties.getProperty("FIRST_INDEX",config.getBENCHMARK_INDEX() + "")));
					config.setFIRST_DEVICE_INDEX(config.getBENCHMARK_INDEX() * config.getDEVICE_NUMBER());
				}
				else {
					config.setFIRST_DEVICE_INDEX(0);
				}

				config.setMYSQL_REAL_INSERT_RATE(Double.parseDouble(properties.getProperty("MYSQL_REAL_INSERT_RATE", config.getMYSQL_REAL_INSERT_RATE()+"")));
				config.setREAL_INSERT_RATE(Double.parseDouble(properties.getProperty("REAL_INSERT_RATE", config.getREAL_INSERT_RATE()+"")));
				if(config.getREAL_INSERT_RATE() <= 0 || config.getREAL_INSERT_RATE() > 1) {
                                config.setREAL_INSERT_RATE(1);
                                LOGGER.error("Invalid parameter REAL_INSERT_RATE: {}, whose value range should be (0, "
                  + "1], using default value 1.0", config.getREAL_INSERT_RATE());
        }
				config.setIS_ALL_NODES_VISIBLE(Boolean.parseBoolean(properties.getProperty("USE_CLUSTER_DB",
						String.valueOf(config.isIS_ALL_NODES_VISIBLE()))));
			} catch (IOException e) {
				e.printStackTrace();
			}
			try {
				inputStream.close();
			} catch (IOException e) {
				LOGGER.error("Fail to close config file input stream", e);
			}
		} else {
			LOGGER.warn("{} No config file path, use default config", Constants.CONSOLE_PREFIX);
		}
	}
}
