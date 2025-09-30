package nexus_bmb_soft.menu;

/**
 * Action de menu avec possibilité d'annulation
 * @author BlaiseMUBADI
 */
public class MenuAction {

    protected boolean isCancel() {
        return cancel;
    }

    public void cancel() {
        this.cancel = true;
    }

    private boolean cancel = false;
}
