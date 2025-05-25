package view;

import boardifier.model.ContainerElement;
import boardifier.view.TableLook;
import model.PuissanceXPawnPot;
import java.awt.Graphics;
import java.awt.Color;
import java.awt.Font;

/**
 * Red pot inherits from TableLook, using the constructor for
 * flexible cell sizes and a visible border. It implies that if there is no element in a cell
 * it has a zero size and thus, is not displayed. This is why during the game,
 * the red pot will reduce in size because of pawn are removed from the pot to be placed
 * on the main board. At then end, it will totally disappear.
 */
public class RedPawnPotLook extends TableLook {

    public RedPawnPotLook(int cellHeight, int cellWidth, ContainerElement containerElement) {
        super(containerElement, -1, 1);
    }
    
    // Supprimer l'annotation @Override car ce n'est pas une méthode de la superclasse
    public void paintComponent(Graphics g) {
        // super.paintComponent(g); // Ne pas appeler super car la méthode n'existe pas dans le parent
        
        // Afficher le nombre de pions restants
        PuissanceXPawnPot pot = (PuissanceXPawnPot) element;
        int count = pot.getRemainingPawns();
        
        // Position du texte - conversion explicite de double à int
        int x = (int)(element.getX() * 40);
        int y = (int)(element.getY() * 40) - 20;
        
        // Dessiner un fond pour le texte
        g.setColor(new Color(255, 200, 200));
        g.fillRect(x, y, 110, 20);
        
        // Dessiner le texte
        g.setColor(Color.BLACK);
        g.setFont(new Font("Arial", Font.BOLD, 12));
        g.drawString("Rouges: " + count + " pions", x + 5, y + 15);
    }
}
