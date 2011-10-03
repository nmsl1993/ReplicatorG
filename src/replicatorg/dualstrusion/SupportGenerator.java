package replicatorg.dualstrusion;


import java.io.File;
import java.util.ArrayList;

import java.util.ArrayList;

public class SupportGenerator {


	public static void main(String[] args){

	}

	/**
	 * This method generates support material GCode using your selected support material.
	 * 
	 */
	public static ArrayList<String> generateSupport(ArrayList<String> gcode, String type){
		
		
		DualStrusionWorker.stripWhitespace(gcode);
		DualStrusionWorker.stripEmptyLayers(gcode);
		
		
		if(type.equals("support")){
			gcode = getSupportLayers(gcode);
		}
		else if(type.equals("model")){
			gcode = stripSupportLayers(gcode);
			gcode = stripRaft(gcode);
		}
		/*ArrayList<String> ABS_model;
		ArrayList<String> PLA_support;
		//tall-agon-PLA_support_test-STRIPPED.gcode
		//tall-agon-PLA_support_test.gcode
		//Cathedral_Sanctuary_Left_PLA-support.gcode
		//Cathedral_Sanctuary_Left_PLA-support-STRIPPED.gcode

		File ABS_source = new File("bunny-MODEL.gcode");
		File PLA_source = new File("bunny-SUPPORT.gcode");

		File ABS_destination = new File("bunny-MODEL-STRIPPED.gcode");
		File PLA_destination = new File("bunny-SUPPORT-STRIPPED.gcode");

		ABS_destination.delete();
		PLA_destination.delete();

		ABS_model = DualStrusionWorker.readFiletoArrayList(ABS_source);
		PLA_support = DualStrusionWorker.readFiletoArrayList(PLA_source);

		System.out.println(getEndOfRaft(PLA_support));

		//System.out.println("");

		//DualStrusionWorker.printArrayList(ABS_model);
		//DualStrusionWorker.printArrayList(PLA_support);

		DualStrusionWorker.stripWhitespace(ABS_model);
		DualStrusionWorker.stripWhitespace(PLA_support);

		DualStrusionWorker.stripEmptyLayers(ABS_model);
		DualStrusionWorker.stripEmptyLayers(PLA_support);

		ABS_model = stripSupportLayers(ABS_model);
		PLA_support = getSupportLayers(PLA_support);
		//strip1050s(PLA_support);

		ABS_model = stripRaft(ABS_model);
		//DualStrusionWorker.printArrayList(ABS_model);
		//DualStrusionWorker.printArrayList(PLA_support);

		DualStrusionWorker.writeArrayListtoFile(ABS_model, ABS_destination);
		DualStrusionWorker.writeArrayListtoFile(PLA_support, PLA_destination);*/
	}

	/**
	 * This method uses Regex to delete empty layers or layers filled only with comments
	 * @param gcode
	 */
	public static ArrayList<String> stripSupportLayers(ArrayList<String> gcode)
	{
		int length = gcode.size();
		String obj_temp = getObjectTemp(gcode);
		String support_temp = getSupportTemp(gcode);

		System.out.println("Stripping...");

		//System.out.println("obj:" + obj_temp);
		//System.out.println("sup_temp:" + support_temp);

		String find_sup_temp = "M104 S" + support_temp;
		find_sup_temp = find_sup_temp.replace(".", "\\.");
		find_sup_temp = find_sup_temp + ".*";
		System.out.println("support temp: " + find_sup_temp);

		String find_obj_temp = "M104 S" + obj_temp;
		find_obj_temp = find_obj_temp.replace(".", "\\.");
		find_obj_temp = find_obj_temp + ".*";
		System.out.println("obj temp: " + find_obj_temp);

		double feed_rate = getFeedRate(gcode);
		System.out.println(feed_rate);

		for(int i = getEndOfRaft(gcode); i < length-3;  i++)
		{
			if(gcode.get(i).matches("\\(\\<layer\\>.*\\)"))
			{
				//System.out.println("checking line for layer: " + i);
				//System.out.println(gcode.get(i) + "matches ");
				for(int c=i+1;!gcode.get(c).matches("\\(\\</layer\\>\\)");c++)
				{
					//System.out.println("checking line for endlayer: " + c);
					if(gcode.get(c).matches(find_sup_temp))
					{
						//a++;
						int d = c+1;

						while(d<gcode.size()-1){
							if(gcode.get(d).matches("\\(\\</layer\\>\\)")){
								gcode.subList(c, d-1).clear();
								break;
							}
							else if(gcode.get(d).matches(find_obj_temp)){
								gcode.subList(c, d-1).clear();
								break;
							}
							d++;
						}
					}
				}
			}
			length = gcode.size();
		}
		length = gcode.size();
		/*for(int i = getEndOfRaft(gcode); i<length-1; i++ ){
			System.out.println("1050 checking line: " + i);
			if(gcode.get(i).matches("G1.*"))
			{
				if(!gcode.get(i).matches(".*F2100.0")){
					System.out.println("1050 MATCHED LINE: " + i );
					gcode.remove(i);
					i--;
					length--;
				}
			}
			//length=gcode.size();
		}*/
		return gcode;
	}

	/**
	 * This method checks for the end of the initial raft laydown.
	 * 
	 * @param gcode
	 * @return
	 */
	public static int getEndOfRaft(ArrayList<String> gcode){
		int raftEnd = 0;
		for(int i=0;i<gcode.size()-1;i++){
			if(gcode.get(i).matches("\\(\\<raftLayerEnd\\>.*$")){
				raftEnd = i;
			}
		}
		return raftEnd;
	}

	/**
	 * Gets the temperature of the object gcode.
	 * 
	 * @param gcode
	 * @return
	 */
	public static String getObjectTemp(ArrayList<String> gcode){

		String temp = "";

		for(int i=0;i<gcode.size()-1;i++){
			if(gcode.get(i).matches("\\(\\<baseTemperature\\>.*")){
				temp = gcode.get(i).substring(gcode.get(i).indexOf(">")+2, gcode.get(i).lastIndexOf("<")-1);
				break;
			}
		}

		return temp;
	}

	/**
	 * This method returns, in the format of a double, the operating feed rate per second from the gcode.
	 * 
	 * @param gcode
	 * @return
	 */
	public static double getFeedRate(ArrayList<String> gcode){

		String rate = "";

		for(int i=0;i<gcode.size()-1;i++){
			if(gcode.get(i).matches("\\(\\<operatingFeedRatePerSecond\\>.*")){
				rate = gcode.get(i).substring(gcode.get(i).indexOf(">")+2, gcode.get(i).lastIndexOf("<")-1);
				break;
			}
		}

		double parsed = Double.parseDouble(rate);

		return parsed;
	}
	/**
	 * This method gets the temperature of the support layers for stripping.
	 * 
	 * @param gcode
	 * @return
	 */

	public static String getSupportTemp(ArrayList<String> gcode){

		String temp = "";

		for(int i=0;i<gcode.size()-1;i++){
			if(gcode.get(i).matches("\\(\\<supportLayersTemperature\\>.*")){
				temp = gcode.get(i).substring(gcode.get(i).indexOf(">")+2, gcode.get(i).lastIndexOf("<")-1);
				break;
			}
		}

		return temp;

	}

	/**
	 * This method will strip the raft layers out of the gcode being used for the model.
	 * 
	 * @param gcode
	 * @return
	 */
	public static ArrayList<String> stripRaft(ArrayList<String> gcode){

		int startStrip = 0, endStrip = 0;

		for(int i=0;i<gcode.size()-1;i++){
			if(gcode.get(i).matches("\\(\\<layer\\>.*")){
				startStrip = i;
				for(int c=i+1;c<gcode.size()-1;c++){
					if(gcode.get(c).matches("\\(\\<raftLayerEnd\\>.*")){
						endStrip = c;
						break;
					}
				}
				break;
			}
		}

		gcode.subList(startStrip, endStrip).clear();
		return gcode;
	}

	/*public static String getSupportFeedrate(ArrayList<String> gcode){



	}*/

	/**
	 * This method uses Regex to delete empty layers or layers filled only with comments
	 * @param gcode
	 */
	public static ArrayList<String> getSupportLayers(ArrayList<String> gcode){

		int length = gcode.size();
		String obj_temp = getObjectTemp(gcode);
		String support_temp = getSupportTemp(gcode);

		System.out.println("Getting...");

		//System.out.println("obj:" + obj_temp);
		//System.out.println("sup_temp:" + support_temp);


		System.out.println("sup temp/objtemp: " + support_temp + "/" + obj_temp);

		String find_sup_temp = "M104 S" + support_temp;
		find_sup_temp = find_sup_temp.replace(".", "\\.");
		find_sup_temp = find_sup_temp + ".*";
		System.out.println("support temp: " + find_sup_temp);

		String find_obj_temp = "M104 S" + obj_temp;
		find_obj_temp = find_obj_temp.replace(".", "\\.");
		find_obj_temp = find_obj_temp + ".*";
		System.out.println("obj temp: " + find_obj_temp);

		double feed_rate = getFeedRate(gcode);
		System.out.println(feed_rate);

		for(int i = getEndOfRaft(gcode); i < length-3;  i++)
		{
			if(gcode.get(i).matches("\\(\\<layer\\>.*\\)"))
			{
				//System.out.println("checking line for layer: " + i);
				//System.out.println(gcode.get(i) + "matches ");
				for(int c=i+1;!gcode.get(c).matches("\\(\\</layer\\>\\)");c++)
				{
					//System.out.println("checking line for endlayer: " + c);
					if(gcode.get(c).matches(find_obj_temp))
					{
						//System.out.println("found obj temp");
						int d = c+1;

						while(d<gcode.size()-1){
							if(gcode.get(d).matches("\\(\\</layer\\>\\)")){
								gcode.subList(c, d-1).clear();
								break;
							}
							else if(gcode.get(d).matches(find_sup_temp)){
								gcode.subList(c, d-1).clear();
								break;
							}
							d++;
						}
					}
				}
			}
			length = gcode.size();
		}
		String F_to_strip = Integer.toString((int)feed_rate*60);
		//F_to_strip = F_to_strip.replace(".", "\\\\.");
		F_to_strip = ".*F" + F_to_strip + ".*";
		System.out.println("F to strip: " + F_to_strip);

		length = gcode.size();
		for(int i = getEndOfRaft(gcode); i<length-1 && !gcode.get(i).matches("\\(\\<\\/extrusion\\>\\)"); i++ ){
			//System.out.println("1050 checking line: " + i);
			if(gcode.get(i).matches("G1.*"))
			{

				if(!gcode.get(i).matches(F_to_strip)){
					System.out.println("1050 MATCHED LINE: " + i );
					gcode.remove(i);
					i--;
					length--;
				}
			}
			//else
			//{ System.out.println("G MATCHED: " + i);}
			//length=gcode.size();
		}
		/*for(int i = getEndOfRaft(gcode); i<length-1; i++ ){
			System.out.println("M104 checking line: " + i);
			if(gcode.get(i).matches("M104 S15\\.0"))
			{
				System.out.println("M104 MATCHED LINE: " + i );
				gcode.set(i, "M104 S225.0");
			}
		}
		 */
		return gcode;
	}

	public static void strip1050s(ArrayList<String> gcode){
		int length = gcode.size();
		for(int i = 0; i<length-1; i++ ){
			//System.out.println("1050 checking line: " + i);
			if(gcode.get(i).matches(".*F1050.*\n"))
			{
				System.out.println("1050 MATCHED LINE: " + i);
				gcode.remove(i);
				i--;
				length--;
			}
			//length=gcode.size();
		}
	}




}
