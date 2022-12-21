package fr.nkri.crates.objects;

import java.util.List;

public class Gain {
	
	private final String ID;
	private final GainType gainType;
	private final String item;
	private final String name;
	private final int pourcentage;
	private final int Items;
	private final List<String> commands;
	
	public Gain(String ID, GainType gainType, String item, String name, int pourcentage, int Items, List<String> commands)
	{
		this.ID = ID;
		this.gainType = gainType;
		this.item = item; 
		this.name = name; 
		this.pourcentage = pourcentage;
		this.Items = Items;
		this.commands = commands;
	}
	
	public enum GainType
	{ 
		COMMAND;
	}

	public String getID() {
		return ID;
	}

	public GainType getGainType() {
		return gainType;
	}

	public String getItem() {
		return item;
	}

	public String getName() {
		return name;
	}

	public int getPourcentage() {
		return pourcentage;
	}

	public List<String> getCommands() {
		return commands;
	}

	public int getItems() {
		return Items;
	}

}
