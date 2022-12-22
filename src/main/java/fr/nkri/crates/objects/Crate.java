package fr.nkri.crates.objects;

import fr.nkri.crates.utils.ColorCrates;

import java.util.List;

public class Crate 
{
	private final String name;
	private final List<Gain> gains;
	private final Clef clef;
	private final double knockback;
	private final boolean broadcast;
	private final boolean firework;
	private final boolean preview;
	private final String block;
	private final ColorCrates colorCrates;
	private final int MaxItemGains;
	
	public Crate(String name, List<Gain> gains, Clef clef, double knockback, boolean broadcast, boolean firework, boolean preview, String block, ColorCrates colorCrates, int MaxItemGains)
	{
		this.name = name;
		this.gains = gains;
		this.clef = clef;
		this.knockback = knockback;
		this.broadcast = broadcast;
		this.firework = firework;
		this.preview = preview;
		this.block = block;
		this.colorCrates = colorCrates;
		this.MaxItemGains = MaxItemGains;
	}

	public String getName() {
		return name;
	}

	public List<Gain> getGains() {
		return gains;
	}

	public Clef getClef() {
		return clef;
	}


	public String getBlock() {
		return block;
	}


	public int getMaxItemGains() {
		return MaxItemGains;
	}


}
