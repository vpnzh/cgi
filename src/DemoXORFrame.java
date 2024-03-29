import java.awt.Cursor;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JCheckBoxMenuItem;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JSeparator;

/**
 * @author Vladislav Pinzhuro, n. 34224
 * @author Joao Costa, n. 41726
 */
public class DemoXORFrame extends JFrame {

	private static final long serialVersionUID = 1L;
	
	public DemoXORFrame(){
		setSize(WIDTH, HEIGHT);

		JMenuBar barra = new JMenuBar();
		barra.add(criarMenuFicheiro());
		barra.add(criarMenuMenu1());
		barra.add(criarMenuResize());
		barra.add(criarMenuOpcoes());
		barra.add(criarMenuContinuidades());
		barra.add(criarMenuAjuda());
		setJMenuBar(barra);
		
		painel = new DemoXORPanel();

		painel.limparDesenho();
		painel.setCursor(Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR));
		setContentPane(painel);
	}
	
	private JMenu criarMenuContinuidades() {
		JMenu menu = new JMenu("Continuity Options");
		menu.add(criarItemMenuContinuidades("C0G0"));
		menu.add(criarItemMenuContinuidades("C0G1"));
		menu.add(criarItemMenuContinuidades("C1G1"));
		return menu;
	}
	
	private JMenu criarMenuResize() {
		JMenu menu = new JMenu("Resize Options");
		menu.add(criarItemMenuResize("Resize uniforme"));
		menu.add(criarItemMenuResize("Resize dinamico"));
		return menu;
	}
	
	private JMenu criarMenuOpcoes() {
		JMenu menu = new JMenu("Visualization Options");
		menu.add(criarItemMenuOpcoes("Polyline"));
		menu.add(criarItemMenuOpcoes("Bounding box"));
		menu.add(criarItemMenuOpcoes("Bezier curve"));
		menu.add(criarItemMenuOpcoes("B-spline curve"));
		menu.add(criarItemMenuOpcoes("Catmull-Rom curve"));
		return menu;
	}

	private JMenu criarMenuFicheiro() {
		JMenu menu = new JMenu("File");
		menu.add(criarItemMenuFicheiro("New"));
		menu.add(criarItemMenuFicheiro("Print"));
		menu.add(new JSeparator());
		menu.add(criarItemMenuFicheiro("Exit"));
		return menu;
	}
	
	private JMenuItem criarItemMenuResize(String texto) {
		JMenuItem item = new JMenuItem(texto);
		
		class ListenerItemMenu implements ActionListener
		{
			public void actionPerformed(ActionEvent e) {
				if(e.getActionCommand().equals("Resize uniforme"))
					painel.setResize(0);
				else if(e.getActionCommand().equals("Resize dinamico"))
					painel.setResize(1);
			}	
		}
		
		item.addActionListener(new ListenerItemMenu());
		return item;	
	}
	
	private JMenuItem criarItemMenuContinuidades(String texto) {
		JMenuItem item = new JMenuItem(texto);
		
		class ListenerItemMenu implements ActionListener
		{
			public void actionPerformed(ActionEvent e) {
				if(e.getActionCommand().equals("C0G0"))
					painel.setContinuity(0);
				else if(e.getActionCommand().equals("C0G1"))
					painel.setContinuity(1);
				else if(e.getActionCommand().equals("C1G1"))
					painel.setContinuity(2);
			}	
		}
		
		item.addActionListener(new ListenerItemMenu());
		return item;	
	}
	
	private JMenuItem criarItemMenuOpcoes(String texto) {
		
		final JCheckBoxMenuItem item = new JCheckBoxMenuItem(texto);
		
		if(texto.equals("Polyline"))
			item.setSelected(true);

		class ListenerItemMenu implements ActionListener
		{
			public void actionPerformed(ActionEvent e) {
				
				if(e.getActionCommand().equals("Bounding box")) {
					
					if(item.isSelected())
						item.setSelected(true); 
					else
						item.setSelected(false);
					
					painel.changeOption(1);
				}
				else if(e.getActionCommand().equals("Bezier curve")) {
				
					if(item.isSelected())
						item.setSelected(true); 
					else
						item.setSelected(false);
					
					painel.changeOption(2);
				}
				else if(e.getActionCommand().equals("Polyline")) {
					
					if(item.isSelected())
						item.setSelected(true); 
					else
						item.setSelected(false);
					
					painel.changeOption(3);
				}
				else if(e.getActionCommand().equals("B-spline curve")) {
					
					if(item.isSelected())
						item.setSelected(true); 
					else
						item.setSelected(false);
					
					painel.changeOption(4);
				}
				else if(e.getActionCommand().equals("Catmull-Rom curve")) {
					
					if(item.isSelected())
						item.setSelected(true); 
					else
						item.setSelected(false);
					
					painel.changeOption(5);
				}
			}	
		}
		item.addActionListener(new ListenerItemMenu());
		return item;	
	}
	
	private JMenuItem criarItemMenuFicheiro(String texto)
	{
		JMenuItem item = new JMenuItem(texto);
		
		class ListenerItemMenu implements ActionListener
		{

			public void actionPerformed(ActionEvent e) {
				if(e.getActionCommand().equals("New"))
					painel.limparDesenho();
				else if(e.getActionCommand().equals("Print"))
					painel.imprimir();
				else if(e.getActionCommand().equals("Exit"))
					System.exit(0);
			}	
		}
		
		item.addActionListener(new ListenerItemMenu());
		return item;	
	}
	
	
	private JMenu criarMenuMenu1()
	{
		JMenu menu = new JMenu("Pontos");
		menu.add(criarItemMenuMenu1("4"));
		menu.add(criarItemMenuMenu1("7"));
		menu.add(criarItemMenuMenu1("10"));
		return menu;
	}
	
	private JMenuItem criarItemMenuMenu1(String texto)
	{
		JMenuItem item = new JMenuItem(texto);
		
		class ListenerItemMenu implements ActionListener
		{
			public void actionPerformed(ActionEvent event)
			{
				
				if(event.getActionCommand().equals("4")) {
					System.out.println("4 pontos escolhidos."); 
					painel.setPointNumber(4);
				}
				if(event.getActionCommand().equals("7")) {
					System.out.println("7 pontos escolhidos.");
					painel.setPointNumber(7);
				}
				if(event.getActionCommand().equals("10")){
					System.out.println("10 pontos escolhidos.");
					painel.setPointNumber(10);
				}
					
			}
		}
		
		item.addActionListener(new ListenerItemMenu());
		
		return item;
	}
	
	private JMenuItem criarItemMenuAjuda(String texto) 
	{
		JMenuItem item = new JMenuItem(texto);

		class ListenerItemMenu implements ActionListener
		{
			public void actionPerformed(ActionEvent e)
			{
				if(e.getActionCommand().equals("About"))
					JOptionPane.showMessageDialog(null, 
							"Trabalho PrÃƒÂ¡tico 1 \n - Vladislav Pinzhuro, 34224\n - JoÃƒÂ£o Costa, 41726");
			}
		}
		
		item.addActionListener(new ListenerItemMenu());
		return item;
	}
	private JMenu criarMenuAjuda()
	{
		JMenu menu = new JMenu("Help");		
		
		menu.add(criarItemMenuAjuda("About"));
		return menu;
	}
	
	private DemoXORPanel painel;
	
	private static final int WIDTH = 800;
	private static final int HEIGHT= 600;

}