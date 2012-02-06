package com.ybcx.ipintu.db;


public class ApplycantsTable {
	
		
		public static final String TABLE_NAME = "t_applycants";
				
		public static class Columns {			
			public static final String ID = "id";
			public static final String ACCOUNT = "a_account";
			public static final String APPLYREASON = "a_applyReason";
			public static final String APPLYTIME = "a_applyTime";			
		}
		
		public static String getCreateSQL() {
            String createString = TABLE_NAME + "( "             		
            		+ Columns.ID + " TEXT PRIMARY KEY, "
                    + Columns.ACCOUNT + " TEXT, "
            		+ Columns.APPLYREASON + " TEXT, "
                    + Columns.APPLYTIME + " DATE " + ");";

            return "CREATE TABLE " + createString;
		}
		public static String getDropSQL() {
            return "DROP TABLE " + TABLE_NAME;
        }
		public static String[] getIndexColumns() {
			return new String[] {
					Columns.ID,Columns.ACCOUNT,
					Columns.APPLYREASON,Columns.APPLYTIME
			};
		}

	
	
} //end of PintuTables
