package fr.nkri.crates.objects;

public class Clef {
	private final String  item;
	private final String name;
	private final boolean enchanted;
	
	public Clef(String item, String name, boolean enchanted)
	{
		this.item = item;
		this.name = name;
		this.enchanted = enchanted;
	}

	public String getItem() {
		return item;
	}

	public String getName() {
		return name;
	}

	public boolean isEnchanted() {
		return enchanted;
	}
}
