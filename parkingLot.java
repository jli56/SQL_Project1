import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Scanner;
import java.util.Random;
// import these two to get current Date and Year
import java.util.Date;
import java.util.Calendar;

public class parkingLot {
	// ----oracle server
	static final String jdbcURL = "jdbc:oracle:thin:@106.55.242.20:1521:orcl";
	static final String user = "test";
	static final String passwd = "test";
	static final int maxVehicleNum = 2;

	static boolean debugflag = false;
	public static void debug_printlog(String text) {
		if (debugflag) {
			System.out.println(text);
		}
	}

	public static void close(final Connection conn) {
		if (conn != null) {
			try {
				conn.close();
			} catch (final Throwable whatever) {
			}
		}
	}

	public static void close(final Statement st) {
		if (st != null) {
			try {
				st.close();
			} catch (final Throwable whatever) {
			}
		}
	}

	public static void close(final ResultSet rs) {
		if (rs != null) {
			try {
				rs.close();
			} catch (final Throwable whatever) {
			}
		}
	}

	public static void GetVisitorPermit() // visitor users
	{
		Scanner userInput = new Scanner(System.in);

		System.out.println("Enter lot_identifier:");
		String lot_name = userInput.nextLine();

		System.out.println("Enter car license:");
		String car_license = userInput.nextLine();

		System.out.println("Enter the space type, default value is 'Regular':");
		String space_type = userInput.nextLine();
		space_type = space_type.trim();
		if (space_type.length() == 0) {
			space_type = "Regular";
		}

		System.out.println("Enter parking duration(in hour):");
		String duration = userInput.nextLine();

		System.out.println("Enter space number:");
		String space_num = userInput.nextLine();

		System.out.println("Enter phone number:");
		String phone_num = userInput.nextLine();

		try {
			Connection conn = null;
			ResultSet rs = null;
			Statement stmt = null;
			String sql = null;

			String gen_permitid = "TO_CHAR(sysdate,'yy')" + "||'V'||" + "LPAD(permit_seq.nextval,5,'0')";

			conn = DriverManager.getConnection(jdbcURL, user, passwd);
			stmt = conn.createStatement();

			debug_printlog("SELECT " + gen_permitid + " FROM DUAL");// for debug

			rs = stmt.executeQuery("SELECT " + gen_permitid + " FROM DUAL");// it's not necessary to do select first,
																			// this step is for easier debugging
			rs.next();
			String permitid = rs.getString(1);

			debug_printlog(permitid);// for debug

			sql = null;
			sql = String.format(
					"INSERT INTO t_permit(permitid,zid,start_date,expire_date,space_type,univid,phone,space_num,lotname,status) "
							+ "VALUES('%s','%s',%s,%s,'%s','%s','%s',%s,'%s','%s')",
					permitid, "V", "sysdate", "sysdate" + "+" + duration + "/24", space_type, "", phone_num, space_num,
					lot_name, "valid");
			debug_printlog(sql);// for debug
			stmt.executeUpdate(sql);

			sql = null;
			sql = String.format("INSERT INTO t_license(permitid,car_license) VALUES('%s','%s')", permitid, car_license);
			debug_printlog(sql);// for debug
			stmt.executeUpdate(sql);
			
			sql = null;
			sql = String.format("INSERT INTO t_parkingrecord(car_license,lotname,enterdate,leavedate,space_number,zone) "
					+ "VALUES('%s','%s',%s,%s,%s,'%s')", car_license,lot_name,"sysdate","sysdate+1",space_num,"V");
			debug_printlog(sql);// for debug
			stmt.executeUpdate(sql);
			
			System.out.println("Parking request has been processed!");

			close(rs);
			close(stmt);
			close(conn);

		} catch (Throwable e) {
			System.out.println("Something Wrong!");
			e.printStackTrace();
		}

	};

	public static int visitorExitLot() // visitor users
	{
		// 1.call IssueCitation(),
		// 2.delete record from t_parkingrecord,
		// 3.set t_permit status valid->invalid in the end

		Scanner userInput = new Scanner(System.in);

		System.out.println("Enter car license:");
		String car_license = userInput.nextLine();

		try {
			Connection conn = null;
			ResultSet rs = null;
			Statement stmt = null;
			String sql = null;
			int rowcount = 0;
			String lotname = null;
			String permitid = null;

			conn = DriverManager.getConnection(jdbcURL, user, passwd);
			stmt = conn.createStatement();

			sql = null;
			sql = String.format("select count(*) from t_parkingrecord where car_license = '%s'", car_license);
			rs = stmt.executeQuery(sql);
			rs.next();
			rowcount = rs.getInt(1);

			if (rowcount != 1) {
				System.out.println("Car license[" + car_license + "] have [" + rowcount + "] parking records!");
				close(rs);
				close(stmt);
				close(conn);
				return -1;
			}

			sql = null;
			sql = String.format("select lotname from t_parkingrecord where car_license = '%s'", car_license);
			rs = stmt.executeQuery(sql);
			rs.next();
			lotname = rs.getString(1);

			sql = null;
			sql = String.format("select permitid from t_license where car_license = '%s'", car_license);
			debug_printlog(sql);// for debug
			rs = stmt.executeQuery(sql);
			rs.next();
			permitid = rs.getString(1);

			// IssueCitation here
			IssueCitation(car_license, lotname);

			sql = null;
			sql = String.format("delete from t_parkingrecord where car_license = '%s'", car_license);
			debug_printlog(sql);// for debug
			stmt.executeUpdate(sql);

			sql = null;
			sql = String.format(
					"update t_permit set status = 'invalid' where zid = 'V' and status = 'valid' and permitid = '%s' ",
					permitid);
			debug_printlog(sql);// for debug
			stmt.executeUpdate(sql);

			close(rs);
			close(stmt);
			close(conn);

		} catch (Throwable e) {
			System.out.println("Something Wrong!");
			e.printStackTrace();
		}

		return 0;

	};

	public static int ChangeVehicleList() // university users
	{
		Scanner userInput = new Scanner(System.in);

		System.out.println("Please enter your Permit ID:");
		String permitid = userInput.nextLine();

		System.out.println("Please enter your univid:");
		String univid = userInput.nextLine();

		try {
			Connection conn = null;
			ResultSet rs = null;
			Statement stmt = null;
			String sql = null;
			int rowcount = 0;
			String new_license = null;
			String origin_license = null;

			conn = DriverManager.getConnection(jdbcURL, user, passwd);
			stmt = conn.createStatement();

			// user verification
			sql = null;
			sql = String.format("select count(*) from t_permit where permitid = '%s' and univid = '%s' ", permitid,
					univid);
			rs = stmt.executeQuery(sql);
			rs.next();
			rowcount = rs.getInt(1);

			if (rowcount == 0) {
				System.out.println("Invalid user information! Please check input.");
				close(rs);
				close(stmt);
				close(conn);
				return -1;
			} else {
				System.out.println("Please choose your operation:");
				System.out.println("1--> Add a Vehicle");
				System.out.println("2--> Remove a Vehicle");

				while (userInput.hasNextLine()) {
					String sAction = userInput.nextLine();

					if (sAction.equals("1")) {

						sql = null;
						sql = String.format("select count(*) from t_license where permitid = '%s' ", permitid,
								new_license);
						rs = stmt.executeQuery(sql);
						rs.next();
						rowcount = rs.getInt(1);

						if (rowcount >= maxVehicleNum) {
							System.out.println("You already have [" + maxVehicleNum
									+ "] vehicles, please remove one before adding!");
							close(rs);
							close(stmt);
							close(conn);
							return -1;
						}

						System.out.println("Please enter the new license number of vehicle you want to add:");
						new_license = userInput.nextLine();

						sql = null;
						sql = String.format(
								"select count(*) from t_license where permitid = '%s' and car_license = '%s' ",
								permitid, new_license);
						rs = stmt.executeQuery(sql);
						rs.next();
						rowcount = rs.getInt(1);

						if (rowcount != 0) {
							System.out.println("License number [" + new_license + "] already exist!");
							close(rs);
							close(stmt);
							close(conn);
							return -1;
						} else {
							sql = null;
							sql = String.format("insert into t_license(permitid,car_license) values('%s','%s') ",
									permitid, new_license);
							stmt.executeUpdate(sql);
							System.out.println("License number [" + new_license + "] is added!");

						}

						break;
					} else if (sAction.equals("2")) {
						System.out.println("Please enter the license number of vehicle you want to remove:");
						origin_license = userInput.nextLine();

						sql = null;
						sql = String.format(
								"select count(*) from t_license where permitid = '%s' and car_license = '%s' ",
								permitid, origin_license);
						rs = stmt.executeQuery(sql);
						rs.next();
						rowcount = rs.getInt(1);

						if (rowcount == 0) {
							System.out.println("License number [" + origin_license + "] does not exist!");
							close(rs);
							close(stmt);
							close(conn);
							return -1;
						} else {
							sql = null;
							sql = String.format("delete from t_license where permitid = '%s' and car_license = '%s' ",
									permitid, origin_license);
							stmt.executeUpdate(sql);
							System.out.println("License number [" + origin_license + "] is removed!");
						}

						break;
					} else {
						System.out.println("Wrong input! Please try again.");
					}
				}

			}

			close(rs);
			close(stmt);
			close(conn);

		} catch (Throwable e) {
			System.out.println("Something Wrong!");
			e.printStackTrace();
		}
		return 0;

	};

	public static void IssueCitation(String car_license, String lotname) { // employees role function
		// Question: where should we call this function?

		try {
			Connection conn = null;
			ResultSet rs = null;
			Statement stmt = null;
			String sql = null;
			int CitationFlag = 0; // 0-initial 1-No Permit 2-Invalid Permit 3-Expired Permit
			int rowcount = 0;
			String permitid = null;
			String permitstatus = null;
			String category = null;
			double fee = 0.00;

			conn = DriverManager.getConnection(jdbcURL, user, passwd);
			stmt = conn.createStatement();

			// check No Permit
			sql = null;
			sql = String.format("select count(*) from t_license where car_license = '%s' ", car_license);
			rs = stmt.executeQuery(sql);
			rs.next();
			rowcount = rs.getInt(1);
			if (rowcount == 0) {
				CitationFlag = 1;
				category = "No Permit";
				fee = 40.00;
			} else {
				// get permitid
				// note: for visitor permit, one license may have multiple permitid, the newest
				// permit will always be selected in this case
				sql = null;
				sql = String.format(" SELECT permitid,status FROM t_permit where 1=1 and "
						+ " permitid in (select permitid from t_license where car_license = '%s') "
						+ " order by expire_date desc", car_license);

				rs = stmt.executeQuery(sql);
				rs.next();
				permitid = rs.getString(1);
				permitstatus = rs.getString(2);
				debug_printlog("permitid=" + permitid + ", permitstatus=[" + permitstatus + "]");// for debug

				// check Invalid Permit
				if (permitstatus.equalsIgnoreCase("invalid")) {
					CitationFlag = 2;
					category = "Invalid Permit";
					fee = 20.00;

				} else {// check Expired Permit
					sql = null;
					sql = String.format(
							"select count(*) from t_permit where permitid = '%s' and expire_date < sysdate ", permitid);
					rs = stmt.executeQuery(sql);
					rs.next();
					rowcount = rs.getInt(1);

					if (rowcount > 0) {
						CitationFlag = 3;
						category = "Expired Permit";
						fee = 25.00;

						// update permit status to invalid
						sql = null;
						sql = String.format(
								"update t_permit set status='invalid' where status='valid' and permitid='%s' ",
								permitid);
						stmt.executeUpdate(sql);
					}
				}

			}

			System.out.println("citation flag: " + CitationFlag);
			if (CitationFlag > 0) {
				// generate citation number
				rs = stmt.executeQuery("SELECT LPAD(citation_seq.nextval,5,'0') FROM DUAL");
				rs.next();
				String gen_cnum = rs.getString(1);
				debug_printlog("cnum=" + gen_cnum);// for debug

				// t_citation info
				sql = null;
				String model = null;
				String color = null;
				sql = String.format("select count(*) from t_vehicle where car_license = '%s' ", car_license);
				rs = stmt.executeQuery(sql);
				rs.next();
				rowcount = rs.getInt(1);
				if(rowcount == 1) {
					sql = null;
					sql = String.format("select model,color from t_vehicle where car_license = '%s' ", car_license);
					rs = stmt.executeQuery(sql);
					rs.next();
					model = rs.getString(1);
					color = rs.getString(2);
				}


				sql = null;
				sql = String.format(
						"insert into t_citation(cnum,car_license,model,color,lotname,citation_date,payment_due,category,fee,status) "
								+ "values('%s','%s','%s','%s','%s',%s, %s,'%s',%f,'%s')",
						gen_cnum, car_license, model, color, lotname, "sysdate", "sysdate+30", category, fee, "Unpaid");
				debug_printlog(sql);// for debug
				stmt.executeUpdate(sql);

				// notification info
				if (CitationFlag != 1) {
					sql = null;
					sql = String.format(
							" SELECT count(*) FROM t_permit where permitid = '%s' and (univid is not null or phone is not null)",
							permitid);
					debug_printlog(sql);// for debug
					rs = stmt.executeQuery(sql);
					rs.next();
					rowcount = rs.getInt(1);

					if (rowcount == 0) {
						System.out.println("both univid and phone is null in t_permit!");
					} 
					else {
						sql = null;
						sql = String.format(
								" SELECT univid,phone FROM t_permit where permitid = '%s' and (univid is not null or phone is not null)",
								permitid);
						debug_printlog(sql);// for debug
						rs = stmt.executeQuery(sql);
						rs.next();
						String univid = rs.getString(1);
						String phone = rs.getString(2);

						sql = null;
						sql = String.format("insert into t_notification(cnum,univid,phone) values('%s','%s','%s') ", "",
								univid, phone);
						stmt.executeUpdate(sql);
					}
				}
			}
			close(rs);
			close(stmt);
			close(conn);

		} catch (Throwable e) {
			System.out.println("Something Wrong!");
			e.printStackTrace();
		}

	};

	public static void PayCitation(String citation_num) {

		try {
			Connection conn = null;
			ResultSet rs = null;
			Statement stmt = null;
			String sql = null;

			conn = DriverManager.getConnection(jdbcURL, user, passwd);
			stmt = conn.createStatement();

			sql = null;
			sql = String.format("update t_citation set status = 'Paid' where status = 'Unpaid' and cnum = '%s'",
					citation_num);
			debug_printlog(sql);// for debug
			stmt.executeUpdate(sql);

			close(rs);
			close(stmt);
			close(conn);

		} catch (Throwable e) {
			System.out.println("Something Wrong!");
			e.printStackTrace();
		}

	};

	// admin func start//
	public static void AddLot() {

		Scanner userInput = new Scanner(System.in);
		System.out.println("Enter lot_identifier");
		String lot_name = userInput.nextLine();
		System.out.println("Enter Address of the lot");
		String lot_address = userInput.nextLine();
		System.out.println("Enter space start");
		Integer space_start = userInput.nextInt();
		System.out.println("Enter space end");
		Integer space_end = userInput.nextInt();
		System.out.println("Enter vspace start");
		Integer vspace_start = userInput.nextInt();
		System.out.println("Enter vspace end");
		Integer vspace_end = userInput.nextInt();

		try {
			Connection conn = null;
			Statement stmt = null;
			String sql = null;

			conn = DriverManager.getConnection(jdbcURL, user, passwd);
			stmt = conn.createStatement();
			sql = String.format(
					"INSERT INTO t_parkinglot(lotname,address,space_start,space_end,vspace_start,vspace_end) "
							+ "VALUES('%s','%s',%s,%s,%s,%s)",
					lot_name, lot_address, space_start, space_end, vspace_start, vspace_end);
			stmt.executeUpdate(sql);
			stmt.close();
		} catch (SQLException e) {
			System.out.println("Something Wrong!");
			e.printStackTrace();
		}

	};

	public static void AssignZoneToLot() {

		try {
			Connection conn = null;
			ResultSet rs = null;
			Statement stmt = null;
			String sql = null;

			conn = DriverManager.getConnection(jdbcURL, user, passwd);
			stmt = conn.createStatement();
			sql = "SELECT lotname FROM t_parkinglot";
			rs = stmt.executeQuery(sql);
			// give a lot list
			System.out.println("Here is the parking lot list");
			while (rs.next()) {
				String lotname = rs.getString("lotname");
				System.out.println(lotname);
			}
			Scanner userInput = new Scanner(System.in);
			System.out.println("Please choose the lot you want to Assign");
			String lot_name = userInput.nextLine();

			sql = String.format("SELECT lotname FROM t_parkinglot WHERE lotname = '%s'", lot_name);
			rs = stmt.executeQuery(sql);
			// if this lot does not exist
			if (!rs.next()) {
				System.out.println("This lot is invalid");
				close(rs);
				close(stmt);
				close(conn);
				return;
			}
			System.out.println("Enter zone that you want to add");
			String zone_id = userInput.nextLine();
			// insert lot_name and zone_id into t_zone
			sql = String.format("INSERT INTO t_zone(lotname,zid) " + "VALUES('%s','%s')", lot_name, zone_id);
			stmt.executeUpdate(sql);
			stmt.close();
		} catch (SQLException e) {
			System.out.println("Something Wrong!");
			e.printStackTrace();
		}

	};

	public static void AssignTypeToSpace() {

		Scanner userInput = new Scanner(System.in);
		System.out.println("Enter the lot you want to Assign");
		String lot_name = userInput.nextLine();
		System.out.println("Enter the space type");
		String space_type = userInput.nextLine();
		System.out.println("Enter start number");
		Integer start_num = userInput.nextInt();
		System.out.println("Enter end number");
		Integer end_num = userInput.nextInt();

		try {
			Connection conn = null;
			Statement stmt = null;
			String sql = null;
			conn = DriverManager.getConnection(jdbcURL, user, passwd);
			stmt = conn.createStatement();
			sql = String.format( "INSERT INTO t_spacetype(lotname,space_type,start_num,end_num) " 
					+ "VALUES('%s','%s',%s,%s)",lot_name,space_type,start_num,end_num);
			stmt.executeUpdate(sql);
			stmt.close();
		} catch (SQLException e) {
			System.out.println("Something Wrong!");
			e.printStackTrace();
		}

	};

	public static void AssignPermit() {
		Scanner userInput = new Scanner(System.in);
		System.out.println("Enter your univID");
		String univ_id = userInput.nextLine();
		// get status of this univID from DB
		try {
			Connection conn = null;
			ResultSet rs = null;
			Statement stmt = null;
			String sql = null;

			conn = DriverManager.getConnection(jdbcURL, user, passwd);
			stmt = conn.createStatement();
			sql = String.format("SELECT status FROM t_univuser WHERE univid = '%s' ", univ_id);
			rs = stmt.executeQuery(sql);
			// if this univ_id does not exist
			if (!rs.next()) {
				System.out.println("This univID not exist in our university");
				close(rs);
				close(stmt);
				close(conn);
				return;
			}
			// rs.next();

			// this univ_id exist and get his/her status
			String status = rs.getString(1);
			System.out.println(status);
			System.out.println("Enter the lot you want to Assign");
			String lot_name = userInput.nextLine();

			sql = String.format("SELECT zid FROM t_zone WHERE lotname = '%s' ", lot_name);
			rs = stmt.executeQuery(sql);
			System.out.println("Here is the Zone of your lot");
			while (rs.next()) {
				String zone_id = rs.getString("zid");
				System.out.println(zone_id);
			}
			System.out.println("Please choose oneEnter zone here");
			String zid = userInput.nextLine();
			// check if the zid is valid
			sql = String.format("SELECT * FROM t_zone WHERE lotname = '%s' AND zid = '%s'", lot_name, zid);
			rs = stmt.executeQuery(sql);
			// if this univ_id does not exist
			if (!rs.next()) {
				System.out.println("This zid is invalid");
				close(rs);
				close(stmt);
				close(conn);
				return;
			}

			// generate pid first 2 digit: year
			int year = Calendar.getInstance().get(Calendar.YEAR);
			String str_year = String.valueOf(year);
			String pid = str_year.substring(2) + zid;
			// we generate 4 or 5 random digit or letter for permit_id
			String RANDOM_TABLE = "ABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890";
			Random rnd = new Random();
			// generate pid:
			boolean pid_flag = false;
			while (!pid_flag) {
				pid = str_year.substring(2) + zid;
				while (pid.length() < 8) {
					int index = (int) (rnd.nextFloat() * RANDOM_TABLE.length());
					pid += RANDOM_TABLE.charAt(index);
				}
				// check if the new generate pid is
				sql = String.format("SELECT * FROM t_permit WHERE permitid = '%s'", pid);
				rs = stmt.executeQuery(sql);
				// if we do not have record in permit database
				if (!rs.next()) {
					// break to loop
					pid_flag = true;
				}
				debug_printlog("pid="+pid);// for debug

			}
			// add different month depend on status
			String period = "";
			if (status == "S") {
				// 4 month add for student
				period = "4";
			} else {
				// 12 month add for Employee
				period = "12";
			}

			// space_type
			System.out.println("Enter the space type, default value is 'Regular':");
			String permit_space_type = userInput.nextLine();
			String space_type = permit_space_type.trim();
			if (permit_space_type.length() == 0) {
				space_type = "Regular";
			}

			// user input car information
			System.out.println("Enter your car license");
			String car_license = userInput.nextLine();
			System.out.println("Enter the manufacturer of your car ");
			String manufacturer = userInput.nextLine();
			System.out.println("Enter the model of your car ");
			String model = userInput.nextLine();
			System.out.println("Enter the year of your car ");
			String car_year = userInput.nextLine();
			System.out.println("Enter the color of your car ");
			String color = userInput.nextLine();

			// save car information to t_vehicle db
			sql = String.format("INSERT INTO t_vehicle(car_license,manufacturer,model,year,color) "
					+ "VALUES('%s','%s','%s',%s,'%s')", car_license, manufacturer, model, car_year, color);
			debug_printlog(sql);// for debug
			stmt.executeUpdate(sql);

			// save pid to t_permit db
			sql = String.format(
					"INSERT INTO t_permit(permitid,zid,start_date,expire_date,space_type,univid,phone,space_num,lotname,status) "
							+ "VALUES('%s','%s',%s,%s,'%s','%s','%s',%s,'%s','%s')",
					pid, zid, "sysdate", "add_months(sysdate," + period + ")", space_type, univ_id, "", 0,lot_name, "valid");
			debug_printlog(sql);// for debug
			stmt.executeUpdate(sql);

			// save t_license and permitid into t_license
			sql = String.format("INSERT INTO t_license(permitid,car_license) " + "VALUES('%s','%s')", pid, car_license);
			debug_printlog(sql);// for debug
			stmt.executeUpdate(sql);

			System.out.println("Finished");
		} catch (SQLException e) {
			System.out.println("Something Wrong!");
			e.printStackTrace();
		}

	};

	public static boolean CheckVValidParking(String space_num, String lotname, String car_license) {
		// use db's sysdate as current date,time
		Connection conn = null;
		ResultSet rs = null;
		Statement stmt = null;
		String sql = null;
		int rowcount = 0;

		try {
			conn = DriverManager.getConnection(jdbcURL, user, passwd);
			stmt = conn.createStatement();

			sql = null;
			sql = String.format(
					"select count(*) from T_PERMIT where status='valid' and expire_date >= sysdate and space_num='%s' and lotname='%s' "
							+ "and permitid in (select permitid from t_license where car_license = '%s') order by expire_date desc",
					space_num, lotname, car_license);
			debug_printlog(sql);// for debug

			rs = null;
			rs = stmt.executeQuery(sql);
			rs.next();
			rowcount = rs.getInt(1);

			debug_printlog("CheckVValidParking rowcnt=" + rowcount);// for debug

			close(rs);
			close(stmt);
			close(conn);

		} catch (Throwable e) {
			System.out.println("Something Wrong!");
			e.printStackTrace();
		}

		if (rowcount > 0) {
			return true;
		} else {
			return false;
		}

	};

	public static boolean CheckNVValidParking(String zid, String permitid) {
		// Input permit ID and parking zone to check if valid?
		// use db's sysdate as current date,time
		Connection conn = null;
		ResultSet rs = null;
		Statement stmt = null;
		String sql = null;
		int rowcount = 0;
		// String space_type = null;
		String enterdate = null;
		String zid_convert = null;
		
		if (zid.equalsIgnoreCase("V")) {
			System.out.println("non-visitor should not park in visitor zone!");
			return false;
		}
		
		if (zid.length() == 1) {
			zid_convert = zid + "X";
			//System.out.println(zid_convert);//for debug
		}

		try {
			conn = DriverManager.getConnection(jdbcURL, user, passwd);
			stmt = conn.createStatement();

			sql = null;
			sql = String.format("select count(*) from t_license where permitid = '%s' ", permitid);
			rs = stmt.executeQuery(sql);
			rs.next();
			rowcount = rs.getInt(1);
			if (rowcount == 0) {
				System.out.println("There is no record of license info with this Permit ID!");
				close(rs);
				close(stmt);
				close(conn);
				return false;
			}

			// sql = null;
			// sql = String.format("select space_type from t_permit where permitid = '%s' ",
			// permitid);
			// rs = stmt.executeQuery(sql);
			// rs.next();
			// space_type = rs.getString(1);

			if (permitid.charAt(2) == 'V') {
				System.out.println("It's not a non-visitor permit!");
				close(rs);
				close(stmt);
				close(conn);
				return false;
			} else if (permitid.charAt(3) == 'S') {
				System.out.println("check Student !");

				sql = null;
				sql = String.format(
						"select to_char(enterdate,'hh24miss') from t_parkingrecord where car_license in (select car_license from t_license where permitid = '%s') ",
						permitid);
				rs = stmt.executeQuery(sql);
				while (rs.next()) {
					enterdate = rs.getString(1);
					if (enterdate.compareTo("080000") > 0 && enterdate.compareTo("170000") < 0 && zid_convert.charAt(1) != 'S') {
						// student enter employee zone between 08:00:00 ~ 17:00:00 (work hours)
						System.out.println("student enter employee zone at "+enterdate);
						close(rs);
						close(stmt);
						close(conn);
						return false;
					}
				}

			} else {
				System.out.println("check Employee !");
				System.out.println("It seems nothing have to do...");
			}

			close(rs);
			close(stmt);
			close(conn);

		} catch (Throwable e) {
			System.out.println("Something Wrong!");
			e.printStackTrace();
		}

		return true;

	};
	// admin func end//

	public static void studentEnterLot() {
		final Scanner userInput = new Scanner(System.in);
		System.out.println("Enter the lot you want to park");
		final String lot_name = userInput.nextLine();
		System.out.println("Enter the space number");
		final String space_string = userInput.nextLine();
		final int space = Integer.parseInt(space_string);
		System.out.println("Enter plate number");
		final String plate = userInput.nextLine();
		System.out.println("Enter zone type");
		final String zone = userInput.nextLine();
		Connection conn = null;
		final ResultSet rs = null;
		Statement stmt = null;
		String sql = null;

		try {
			conn = DriverManager.getConnection(jdbcURL, user, passwd);
			stmt = conn.createStatement();

			sql = null;
			sql = String.format("INSERT INTO t_parkingrecord " + "Values ( '%s', '%s', sysdate, NULL, %d, '%s')",
					lot_name, plate, space, zone);
			debug_printlog(sql);// for debug
			stmt.execute(sql);
			
			System.out.println("Parking request has been processed!");
			
			userInput.close();
			close(rs);
			close(stmt);
			close(conn);

		} catch (final Throwable e) {
			System.out.println("Something Wrong!");
			e.printStackTrace();
		}
	}

	public static void studentExitLot() {
		final Scanner userInput = new Scanner(System.in);
		System.out.println("Enter plate number");
		final String plate = userInput.nextLine();
		Connection conn = null;
		final ResultSet rs = null;
		Statement stmt = null;
		String sql = null;

		try {
			conn = DriverManager.getConnection(jdbcURL, user, passwd);
			stmt = conn.createStatement();

			sql = String.format("SELECT zone, lotname \r\n" + "FROM t_parkingrecord\r\n" + "WHERE car_license = '%s' ",
					plate);
			debug_printlog(sql);// for debug
			String zone = "";
			String lotname = "";
			final ResultSet result = stmt.executeQuery(sql);
			
			if (result.next()) {
				zone = result.getString(1);
				lotname = result.getString(2);
			} else {
				System.out.println("No parking record for given plate number!");
				System.exit(0);
			}

			IssueCitation(plate, lotname);

			sql = null;
			sql = String.format("delete from t_parkingrecord where car_license = '%s'", plate);
			debug_printlog(sql);// for debug
			stmt.executeUpdate(sql);
			
			userInput.close();
			close(rs);
			close(stmt);
			close(conn);

		} catch (final Throwable e) {
			System.out.println("Something Wrong!");
			e.printStackTrace();
		}
	}

	public static void studentUpdateVihicle(final String uid) {
		final Scanner userInput = new Scanner(System.in);
		System.out.println("Please enter the new vehicle info");
		System.out.println("Enter the plate number");
		final String car_license = userInput.nextLine();
		System.out.println("Enter the manufactuer");
		final String manu = userInput.nextLine();
		System.out.println("Enter model");
		final String model = userInput.nextLine();
		System.out.println("Enter color");
		final String color = userInput.nextLine();
		System.out.println("Enter year of the vehicle");
		final String year_string = userInput.nextLine();
		final int year = Integer.parseInt(year_string);
		Connection conn = null;
		final ResultSet rs = null;
		Statement stmt = null;
		String sql = null;
		try {
			conn = DriverManager.getConnection(jdbcURL, user, passwd);
			stmt = conn.createStatement();

			sql = null;
			sql = String.format("SELECT l.car_license, p.permitid \r\n" + "FROM t_license l, t_permit p\r\n"
					+ "WHERE p.univid = '%s' AND l.permitid = p.permitid ", uid);
			debug_printlog(sql);// for debug
			String license = "";
			String permit = "";
			final ResultSet result = stmt.executeQuery(sql);
			debug_printlog(sql);// for debug
			if (result.next()) {
				license = result.getString(1);
				permit = result.getString(2);

				sql = String.format(
						"DELETE \r\n" + "FROM t_license \r\n" + "WHERE car_license = '%s' AND permitid = '%s'", license,
						permit);
				debug_printlog(sql);// for debug
				stmt.execute(sql);
				sql = String.format("DELETE \r\n" + "FROM t_vehicle \r\n" + "WHERE car_license = '%s'", license);
				debug_printlog(sql);// for debug
				stmt.execute(sql);
			}
			sql = String.format("INSERT INTO t_license \r\n" + "VALUES('%s','%s')", permit, car_license);
			debug_printlog(sql);// for debug
			stmt.execute(sql);
			sql = String.format("INSERT INTO t_vehicle \r\n" + "VALUES('%s','%s','%s', %d,'%s')", car_license, manu,
					model, year, color);
			debug_printlog(sql);// for debug
			stmt.execute(sql);
		} catch (final Throwable e) {
			System.out.println("Something Wrong!");
			e.printStackTrace();
		}

	}

	public static void employeeEnterLot() {
		final Scanner userInput = new Scanner(System.in);
		System.out.println("Enter the lot you want to park");
		final String lot_name = userInput.nextLine();
		System.out.println("Enter the space number");
		final String space_string = userInput.nextLine();
		final int space = Integer.parseInt(space_string);
		System.out.println("Enter plate number");
		final String plate = userInput.nextLine();
		System.out.println("Enter zone type");
		final String zone = userInput.nextLine();
		Connection conn = null;
		final ResultSet rs = null;
		Statement stmt = null;
		String sql = null;

		try {
			conn = DriverManager.getConnection(jdbcURL, user, passwd);
			stmt = conn.createStatement();

			sql = null;
			sql = String.format("INSERT INTO t_parkingrecord " + "Values ( '%s', '%s', sysdate, NULL, %d, '%s')",
			plate, lot_name, space, zone);
			debug_printlog(sql);// for debug

			stmt.execute(sql);
			
			System.out.println("Parking request has been processed!");
			
			userInput.close();
			close(rs);
			close(stmt);
			close(conn);

		} catch (final Throwable e) {
			System.out.println("Something Wrong!");
			e.printStackTrace();
		}
	}

	public static void employeeExitLot() {
		final Scanner userInput = new Scanner(System.in);
		System.out.println("Enter plate number");
		final String plate = userInput.nextLine();
		Connection conn = null;
		final ResultSet rs = null;
		Statement stmt = null;
		String sql = null;

		try {
			conn = DriverManager.getConnection(jdbcURL, user, passwd);
			stmt = conn.createStatement();

			sql = String.format("SELECT zone, lotname \r\n" + "FROM t_parkingrecord\r\n" + "WHERE car_license = '%s' ",
					plate);
			debug_printlog(sql);// for debug
			String zone = "";
			String lotname = "";
			final ResultSet result = stmt.executeQuery(sql);
			if (result.next()) {
				zone = result.getString(1);
				lotname = result.getString(2);
			} else {
				System.out.println("No parking record for given plate number!");
				System.exit(0);
			}
			
			IssueCitation(plate, lotname);
			
			sql = null;
			sql = String.format("delete from t_parkingrecord where car_license = '%s'", plate);
			debug_printlog(sql);// for debug
			stmt.executeUpdate(sql);
			
			userInput.close();
			close(rs);
			close(stmt);
			close(conn);

		} catch (final Throwable e) {
			System.out.println("Something Wrong!");
			e.printStackTrace();
		}
	}

	public static void employeeUpdateVihicle(final String uid) {
		final Scanner userInput = new Scanner(System.in);
		System.out.println("Enter the plate number you want to update. Enter 'new' for new vehicle record.");
		final String plate = userInput.nextLine();
		System.out.println("Please enter the new vehicle info");
		System.out.println("Enter the plate number");
		final String car_license = userInput.nextLine();
		System.out.println("Enter the manufactuer");
		final String manu = userInput.nextLine();
		System.out.println("Enter model");
		final String model = userInput.nextLine();
		System.out.println("Enter color");
		final String color = userInput.nextLine();
		System.out.println("Enter year of the vehicle");
		final String year_string = userInput.nextLine();
		final int year = Integer.parseInt(year_string);
		Connection conn = null;
		final ResultSet rs = null;
		Statement stmt = null;
		String sql = null;
		try {
			conn = DriverManager.getConnection(jdbcURL, user, passwd);
			stmt = conn.createStatement();

			sql = null;
			if (plate.equals("new")) {
				sql = String.format("SELECT count(*) \r\n" + "FROM t_license l, t_permit p\r\n"
						+ "WHERE p.univid = '%s' AND l.permitid = p.permitid ", uid);
				debug_printlog(sql);// for debug
				ResultSet result = stmt.executeQuery(sql);
				String count = "";
				String permit = "";
				if (result.next()) {
					count = result.getString(1);
					debug_printlog("count is::::::: " + count);// for debug

					if (Integer.parseInt(count) < 2) {
						sql = String.format("SELECT p.permitid \r\n" + "FROM t_license l, t_permit p\r\n"
								+ "WHERE p.univid = '%s' AND l.permitid = p.permitid ", uid);
						debug_printlog(sql);// for debug
						result = stmt.executeQuery(sql);
						if (result.next()) {
							permit = result.getString(1);
						}
						sql = String.format("INSERT INTO t_license \r\n" + "VALUES('%s','%s')", permit, car_license);
						debug_printlog(sql);// for debug
						stmt.execute(sql);
						sql = String.format("INSERT INTO t_vehicle \r\n" + "VALUES('%s','%s','%s', %d,'%s')",
								car_license, manu, model, year, color);
						debug_printlog(sql);// for debug
						stmt.execute(sql);
					} else {
						System.out.println("You already have 2 records!");
						System.exit(0);
					}
				}
			} else {
				sql = String.format(
						"SELECT l.car_license, p.permitid \r\n" + "FROM t_license l, t_permit p\r\n"
								+ "WHERE p.univid = '%s' AND l.permitid = p.permitid AND l.car_license = '%s'",
						uid, plate);
				debug_printlog(sql);// for debug
				String license = "";
				String permit = "";
				final ResultSet result = stmt.executeQuery(sql);
				debug_printlog(sql);// for debug
				if (result.next()) {
					license = result.getString(1);
					permit = result.getString(2);

					sql = String.format(
							"DELETE \r\n" + "FROM t_license \r\n" + "WHERE car_license = '%s' AND permitid = '%s'",
							license, permit);
					debug_printlog(sql);// for debug
					stmt.execute(sql);
					sql = String.format("DELETE \r\n" + "FROM t_vehicle \r\n" + "WHERE car_license = '%s'", license);
					debug_printlog(sql);// for debug
					stmt.execute(sql);
				} else {
					System.out.println("The given plate number record is not exist in the system.");
					System.exit(0);
				}

				sql = String.format("INSERT INTO t_license \r\n" + "VALUES('%s','%s')", permit, car_license);
				debug_printlog(sql);// for debug
				stmt.execute(sql);
				sql = String.format("INSERT INTO t_vehicle \r\n" + "VALUES('%s','%s','%s', %d,'%s')", car_license, manu,
						model, year, color);
				debug_printlog(sql);// for debug
				stmt.execute(sql);
			}
		} catch (final Throwable e) {
			System.out.println("Something Wrong!");
			e.printStackTrace();
		}

	}

	public static void main(final String[] args) {

			final Scanner userInput = new Scanner(System.in);
			System.out.println("What type of user?");
			System.out.println("1--> Admin Role");
			System.out.println("2--> Employee Role");
			System.out.println("3--> Student Role");
			System.out.println("4--> Visitor Role");
			System.out.println("5--> Pay Citation");
			System.out.println("0--> Exit program");
			String userType = "";
			String action = "";
			while (userInput.hasNextLine()) {
				userType = userInput.nextLine();

				if (userType.equals("1")) {
					// Admin login
					boolean login_success = false;
					System.out.println("Enter the univid");
					String univid = userInput.nextLine();
					System.out.println("Enter password");
					String univ_passwd = userInput.nextLine();

					try {
						Connection conn = null;
						ResultSet rs = null;
						Statement stmt = null;
						String sql = null;

						conn = DriverManager.getConnection(jdbcURL, user, passwd);
						stmt = conn.createStatement();
						sql = String.format("SELECT status FROM t_univuser WHERE univid = '%s' AND passwd = '%s'",
								univid, univ_passwd);
						rs = stmt.executeQuery(sql);
						// if this univ_id does not exist
						if (!rs.next()) {
							System.out.println("Wrong univid or password");
							close(rs);
							close(stmt);
							close(conn);
							return;
						}
						stmt.close();
						// set login_success to true
						login_success = true;
					} catch (SQLException e) {
						System.out.println("Something Wrong!");
						e.printStackTrace();
					}

					while (login_success) {

						System.out.println("Select an action to continue:");
						System.out.println("1--> Add Lot");
						System.out.println("2--> Assign zone to lot");
						System.out.println("3--> Assign type to space");
						System.out.println("4--> Assign permit");
						System.out.println("5--> Check vvalid parking");
						System.out.println("6--> Check nvvalid parking");
						System.out.println("0--> Exit program");
						while (userInput.hasNextLine()) {
							action = userInput.nextLine();

							if (action.equals("1")) {
								AddLot();
								System.exit(0);
							} else if (action.equals("2")) {
								AssignZoneToLot();
								System.exit(0);
							} else if (action.equals("3")) {
								AssignTypeToSpace();
								System.exit(0);
							} else if (action.equals("4")) {
								AssignPermit();
								System.exit(0);
							} else if (action.equals("5")) {
								System.out.println("Please enter car license you want to check");
								String car_license = userInput.nextLine();
								System.out.println("Please enter the lot name you want to check");
								String lotname = userInput.nextLine();
								System.out.println("Please enter the space number you want to check");
								String space_num = userInput.nextLine();
								if(CheckVValidParking(space_num, lotname, car_license)){
									System.out.println("It is valid");
								}
								else{
									System.out.println("It is invalid");
								}
								System.exit(0);

							} else if (action.equals("6")) {
								System.out.println("Please enter permit ID you want to check");
								String permitid = userInput.nextLine();
								System.out.println("Please enter Zone ID you want to check");
								String zid = userInput.nextLine();
								
								if(CheckNVValidParking(zid,permitid)){
									System.out.println("It is valid");
								}
								else{
									System.out.println("It is invalid");
								}
								System.exit(0);
							} else if (action.equals("0")) {
								System.exit(0);
							}

							else {
								System.out.println("Wrong action type please enter again.");
							}
						}

					}

				} else if (userType.equals("2")) {
					// Employee
					boolean login_success = false;
					System.out.println("Enter the univid");
					String univid = userInput.nextLine();
					System.out.println("Enter password");
					String univ_passwd = userInput.nextLine();

					try {
						Connection conn = null;
						ResultSet rs = null;
						Statement stmt = null;
						String sql = null;

						conn = DriverManager.getConnection(jdbcURL, user, passwd);
						stmt = conn.createStatement();
						sql = String.format("SELECT status FROM t_univuser WHERE univid = '%s' AND passwd = '%s'",
								univid, univ_passwd);
						rs = stmt.executeQuery(sql);
						// if this univ_id does not exist
						if (!rs.next()) {
							System.out.println("Wrong univid or password");
							close(rs);
							close(stmt);
							close(conn);
							return;
						}
						stmt.close();
						// set login_success to true
						login_success = true;
					} catch (SQLException e) {
						System.out.println("Something Wrong!");
						e.printStackTrace();
					}

					while (login_success) {

						System.out.println("Select an action to continue:");
						System.out.println("1--> Enter Lot");
						System.out.println("2--> Exit Lot");
						System.out.println("3--> Change vehicle list");
						System.out.println("0--> Exit program");
						while (userInput.hasNextLine()) {
							action = userInput.nextLine();

							if (action.equals("1")) {
								employeeEnterLot();
								System.exit(0);
							} else if (action.equals("2")) {
								employeeExitLot();
								System.exit(0);
							} else if (action.equals("3")) {
								employeeUpdateVihicle(univid);
								System.exit(0);
							} else if (action.equals("0")) {
								System.exit(0);
							}

							else {
								System.out.println("Wrong action type please enter again.");
							}
						}
						System.exit(0);
					}

				} else if (userType.equals("3")) {
					// Student
					boolean login_success = false;
					System.out.println("Enter the univid");
					String univid = userInput.nextLine();
					System.out.println("Enter password");
					String univ_passwd = userInput.nextLine();

					try {
						Connection conn = null;
						ResultSet rs = null;
						Statement stmt = null;
						String sql = null;

						conn = DriverManager.getConnection(jdbcURL, user, passwd);
						stmt = conn.createStatement();
						sql = String.format("SELECT status FROM t_univuser WHERE univid = '%s' AND passwd = '%s'",
								univid, univ_passwd);
						rs = stmt.executeQuery(sql);
						// if this univ_id does not exist
						if (!rs.next()) {
							System.out.println("Wrong univid or password");
							close(rs);
							close(stmt);
							close(conn);
							return;
						}
						stmt.close();
						// set login_success to true
						login_success = true;
					} catch (SQLException e) {
						System.out.println("Something Wrong!");
						e.printStackTrace();
					}

					while (login_success) {

						System.out.println("Select an action to continue:");
						System.out.println("1--> Enter Lot");
						System.out.println("2--> Exit Lot");
						System.out.println("3--> Change vehicle list");
						System.out.println("0--> Exit program");
						while (userInput.hasNextLine()) {
							action = userInput.nextLine();

							if (action.equals("1")) {
								studentEnterLot();
								System.exit(0);
							} else if (action.equals("2")) {
								studentExitLot();
								System.exit(0);
							} else if (action.equals("3")) {
								studentUpdateVihicle(univid);
								System.exit(0);
							} else if (action.equals("0")) {
								System.exit(0);
							}

							else {
								System.out.println("Wrong action type please enter again.");
							}
						}
					}
				} else if (userType.equals("4")) {
					// Visitor
					System.out.println("Select an action to continue:");
					System.out.println("1--> Enter Lot");
					System.out.println("2--> Exit Lot");
					System.out.println("0--> Exit program");
					while (userInput.hasNextLine()) {
						action = userInput.nextLine();

						if (action.equals("1")) {
							GetVisitorPermit();
							System.exit(0);
						} else if (action.equals("2")) {
							visitorExitLot();
							System.exit(0);
						} else if (action.equals("0")) {
							System.exit(0);
						} else {
							System.out.println("Wrong action type please enter again.");
						}
					}
				} else if (userType.equals("5")){
					System.out.println("Please Enter citation number");
					String citation_num = userInput.nextLine();
					PayCitation(citation_num);
					System.exit(0);

				}else if (action.equals("0")) {
					System.exit(0);
				} else {
					System.out.println("Wrong user type please enter again.");
					continue;
				}
			}
	}// main func end

}
