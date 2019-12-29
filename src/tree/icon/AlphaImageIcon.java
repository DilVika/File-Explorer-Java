package tree.icon;

import java.awt.AlphaComposite;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.ImageObserver;
import javax.swing.Icon;
import javax.swing.ImageIcon;

/**
 * An Icon  wrapper that paints the contained icon with a specified transparency.
 * <P>
 * This class is suitable for wrapping an <CODE>ImageIcon</CODE>
 * that holds an animated image.  To show a non-animated Icon with transparency,
 * the companion class {@link AlphaIcon} is a lighter alternative.
 *
 * @version 1.0 08/16/10
 * @author Darryl
 * 
 * https://tips4java.wordpress.com/2010/08/22/alpha-icons/
 */

@SuppressWarnings("serial")
public class AlphaImageIcon extends ImageIcon {
    private Icon icon;
    private Image image;
    private float alpha;

    public AlphaImageIcon(Icon icon, float alpha) {
        this.icon = icon;
        this.alpha = alpha;
    }

    @Override
    public Image getImage() {
        return image;
    }

    @Override
    public void setImage(Image image) {
        if (icon instanceof ImageIcon)
            ((ImageIcon) icon).setImage(image);
    }

    @Override
    public int getImageLoadStatus() {
        if (icon instanceof ImageIcon)
            return ((ImageIcon) icon).getImageLoadStatus();
        return 0;
    }

    @Override
    public ImageObserver getImageObserver() {
        if (icon instanceof ImageIcon)
            return ((ImageIcon) icon).getImageObserver();
        return null;
    }

    @Override
    public void setImageObserver(ImageObserver observer) {
        if (icon instanceof ImageIcon)
            ((ImageIcon) icon).setImageObserver(observer);
    }

    public float getAlpha() {
        return alpha;
    }

    public Icon getIcon() {
        return icon;
    }

    @Override
    public void paintIcon(Component c, Graphics g, int x, int y) {
        if (icon instanceof ImageIcon)
            image = ((ImageIcon) icon).getImage();
        else
            image = null;
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setComposite(AlphaComposite.SrcAtop.derive(alpha));
        icon.paintIcon(c, g2, x, y);
        g2.dispose();
    }

    @Override
    public int getIconWidth() {
        return icon.getIconWidth();
    }

    @Override
    public int getIconHeight() {
        return icon.getIconHeight();
    }
}