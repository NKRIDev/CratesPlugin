package fr.nkri.crates.objects;

import java.util.List;

public class ScrollGain {

	private final int size;
	private final List<Gain> gains;
	
	public ScrollGain(int size, List<Gain> start_gains)
	{
		this.size = size;
		this.gains = start_gains;
	}

	public int getSize() {
		return size;
	}

	public List<Gain> getGains() {
		return gains;
	}
	
	public void scrollAndAddGain(Gain gain) {
		gains.remove(0);
		gains.add(gain);
	}

}
