package nexus_bmb_soft.menu;

/**
 * Interface pour gérer les événements de menu
 * @author BlaiseMUBADI
 */
public interface MenuEvent {

    public void menuSelected(int index, int subIndex, MenuAction action);
}
