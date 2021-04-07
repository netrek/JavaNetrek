package jtrek.data;

import jtrek.Copyright;

public class Ship {
	public final static byte SC = 0;
	public final static byte DD = 1;
	public final static byte CA = 2;
	public final static byte BB = 3;
	public final static byte AS = 4;
	public final static byte SB = 5; 
	public final static byte GA = 6;
	public final static byte AT = 7;
	
	public final static Ship[] SHIPS = new Ship[8];
	static {
		SHIPS[SC] = new Ship();
		SHIPS[SC].abbrev = "SC".toCharArray();
		SHIPS[SC].phaserdamage = 75;
		SHIPS[SC].torpspeed = 16;
		SHIPS[SC].maxspeed = 12;       
		SHIPS[SC].maxfuel = 5000;      
		SHIPS[SC].maxarmies = 2;       
		SHIPS[SC].maxshield = 75;
		SHIPS[SC].maxdamage = 75;      
		SHIPS[SC].maxwpntemp = 1000;   
		SHIPS[SC].maxegntemp = 1000;   
		SHIPS[SC].type = SC;        
		SHIPS[SC].width = 20;          
		SHIPS[SC].height = 20;         
		SHIPS[SC].phaserfuse = 10;
	
		SHIPS[DD] = new Ship();
		SHIPS[DD].abbrev = "DD".toCharArray();
		SHIPS[DD].phaserdamage = 85;
		SHIPS[DD].torpspeed = 14;  
		SHIPS[DD].maxspeed = 10;   
		SHIPS[DD].maxfuel = 7000;  
		SHIPS[DD].maxarmies = 5;   
		SHIPS[DD].maxshield = 85;  
		SHIPS[DD].maxdamage = 85;  											   
		SHIPS[DD].maxwpntemp = 1000;											  
		SHIPS[DD].maxegntemp = 1000;
		SHIPS[DD].width = 20;      
		SHIPS[DD].height = 20;     											   
		SHIPS[DD].type = DD;
		SHIPS[DD].phaserfuse = 10;

		SHIPS[CA] = new Ship();
		SHIPS[CA].abbrev = "CA".toCharArray();
		SHIPS[CA].phaserdamage = 100;
		SHIPS[CA].torpspeed = 12;    
		SHIPS[CA].maxspeed = 9;      
		SHIPS[CA].maxfuel = 10000;   
		SHIPS[CA].maxarmies = 10;    
		SHIPS[CA].maxshield = 100;   
		SHIPS[CA].maxdamage = 100;                                              
		SHIPS[CA].maxwpntemp = 1000;                                          
		SHIPS[CA].maxegntemp = 1000;
		SHIPS[CA].width = 20;        
		SHIPS[CA].height = 20;       
		SHIPS[CA].type = CA;    
		SHIPS[CA].phaserfuse = 10;
				
		SHIPS[BB] = new Ship();				
		SHIPS[BB].abbrev = "BB".toCharArray();
		SHIPS[BB].phaserdamage = 105;											   
		SHIPS[BB].torpspeed = 12;
		SHIPS[BB].maxspeed = 8;   											  
		SHIPS[BB].maxfuel = 14000;
		SHIPS[BB].maxarmies = 6;  											   
		SHIPS[BB].maxshield = 130;											   
		SHIPS[BB].maxdamage = 130;											   
		SHIPS[BB].maxwpntemp = 1000;											  
		SHIPS[BB].maxegntemp = 1000;
		SHIPS[BB].width = 20;     
		SHIPS[BB].height = 20;    											   
		SHIPS[BB].type = BB;
		SHIPS[BB].phaserfuse = 10;
				
		SHIPS[AS] = new Ship();				
		SHIPS[AS].abbrev = "AS".toCharArray();
		SHIPS[AS].phaserdamage = 80;
		SHIPS[AS].torpspeed = 16;
		SHIPS[AS].maxspeed = 8;      
		SHIPS[AS].maxfuel = 6000;    
		SHIPS[AS].maxarmies = 20;    
		SHIPS[AS].maxshield = 80;    
		SHIPS[AS].maxdamage = 200;
		SHIPS[AS].maxwpntemp = 1000;											   
		SHIPS[AS].maxegntemp = 1200;
		SHIPS[AS].width = 20;        
		SHIPS[AS].height = 20;       
		SHIPS[AS].type = AS;
		SHIPS[AS].phaserfuse = 10;
				
		SHIPS[SB] = new Ship();				
		SHIPS[SB].abbrev = "SB".toCharArray();									
		SHIPS[SB].phaserdamage = 120;
		SHIPS[SB].torpspeed = 14;   
		SHIPS[SB].maxfuel = 60000;  
		SHIPS[SB].maxarmies = 25;   
		SHIPS[SB].maxshield = 500;  
		SHIPS[SB].maxdamage = 600;  
		SHIPS[SB].maxspeed = 2;     											   
		SHIPS[SB].maxwpntemp = 1300;											   
		SHIPS[SB].maxegntemp = 1000;
		SHIPS[SB].width = 20;       
		SHIPS[SB].height = 20;      
		SHIPS[SB].type = SB;  
		SHIPS[SB].phaserfuse = 4;

		SHIPS[GA] = new Ship();
		SHIPS[GA].abbrev = "GA".toCharArray();
		SHIPS[GA].phaserdamage = 100;
		SHIPS[GA].torpspeed = 13;    
		SHIPS[GA].maxspeed = 9;      
		SHIPS[GA].maxfuel = 12000;   
		SHIPS[GA].maxarmies = 12;    
		SHIPS[GA].maxshield = 140;   
		SHIPS[GA].maxdamage = 120;   
		SHIPS[GA].maxwpntemp = 1000;
		SHIPS[GA].maxegntemp = 1000;
		SHIPS[GA].width = 20;        
		SHIPS[GA].height = 20;       
		SHIPS[GA].type = GA;    
		SHIPS[GA].phaserfuse = 10;
		
		SHIPS[AT] = new Ship();
		SHIPS[AT].abbrev = "AT".toCharArray();
		SHIPS[AT].phaserdamage = 10000;  
		SHIPS[AT].torpspeed = 30;        
		SHIPS[AT].maxspeed = 60;         
		SHIPS[AT].maxfuel = 12000;       
		SHIPS[AT].maxarmies = 1000;      
		SHIPS[AT].maxshield = 30000;     
		SHIPS[AT].maxdamage = 30000;     
		SHIPS[AT].maxwpntemp = 10000;    
		SHIPS[AT].maxegntemp = 10000;    
		SHIPS[AT].width = 28;            
		SHIPS[AT].height = 28;           
		SHIPS[AT].type = AT;
		SHIPS[AT].phaserfuse = 1;
	}

	public char[] abbrev;
	public int phaserdamage;
	public int maxspeed;
	public int maxfuel;
	public int maxshield;
	public int maxdamage;
	public int maxegntemp;
	public int maxwpntemp;
	public int maxarmies;
	public int width;
	public int height;
	public int type;
	public int torpspeed;
	// phaserfuse has problems because server doesn't always send new phaser
	public int phaserfuse;

	/** getShipString */
	public String getShipString() {
		char[] buffer = { '[', abbrev[0], abbrev[1], ']' };
		return new String(buffer);
	}

	// Make sure no other ships are made
	private Ship() {}

}
