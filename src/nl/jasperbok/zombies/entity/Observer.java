package nl.jasperbok.zombies.entity;

public interface Observer {

	public void notify(Observable observable, String message);
}
