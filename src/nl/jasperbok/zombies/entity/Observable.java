package nl.jasperbok.zombies.entity;

public interface Observable {

	public void registerObserver(Observer observer);
	
	public void unregisterObserver(Observer observer);
}
