/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package main;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Label;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

import multilevel.MultiLevel;
import roundrobin.RoundRobin;
import simos.Algorithm;
import simos.OS;
import simos.Planner;
import simos.Processor;
import simos.Process;
import simos.Resource;
import srjf.SRJF;
import util.GUtil;
import util.Queue;

/**
 *
 * @author jefferson
 */
public class MultiColas extends JFrame {
	
    OS os;
    List<PanelProcesador> procesadores;
    DiagramaGantt gantt;
    OSInfo info;
    
    boolean running;
    
    public MultiColas(int cantidad) {
        super("MultiColas");
        
        os = new OS();
        procesadores = new LinkedList<>();
        gantt = new DiagramaGantt(os.getProcesses());
        info = new OSInfo(os);
        crearBarraMenu();
        
        setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.gridy = 0;
        c.weighty = 1;
        c.fill = c.BOTH;
        
        JPanel panelLogica = new JPanel(new GridLayout(1, cantidad));
        
        for (int i=0; i<cantidad; i++) {
            Planner pl = new MultiLevel(false, true);
            Processor p = new Processor("Processor"+(i+1), pl);
            
            pl.addAlgorithm(new RoundRobin());
            pl.addAlgorithm(new SRJF());
            pl.addAlgorithm(new Algorithm());
            
            os.addProcessor(p);
            
            PanelProcesador pPanel = new PanelProcesador(p);
            procesadores.add(pPanel);
            panelLogica.add(pPanel);
        }        
        panelLogica.revalidate();

        JScrollPane scroll = new JScrollPane(panelLogica);
        
        c.gridx = 0;
        c.weightx = 0.8;
        add(scroll, c);
        
        c.gridx = 3;
        c.weightx = 0.2;
        add(info, c);
        
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(350 * cantidad, 600);
        setVisible(true);
        
        crearListener();
    }
    
    private void crearBarraMenu() {
    	JMenuBar barra = new JMenuBar();
    	
    	JMenu sistema = new JMenu("Sistema");
    	JMenu procesos = new JMenu("Procesos");
    	JMenu recursos = new JMenu("Recursos");
    	JMenu gantt = new JMenu("Gantt");
    	
    	JMenuItem iniciar = new JMenuItem("Iniciar");
    	JMenuItem pausar = new JMenuItem("Pausar");
    	JMenuItem crearProceso = new JMenuItem("Crear");
    	JMenuItem crearRecurso = new JMenuItem("Crear");
    	JMenuItem mostrarGantt = new JMenuItem("Mostrar");
    	
    	final MultiColas gui = this;
    	
    	iniciar.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				running = true;
			}
		});
    	
    	pausar.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				running = false;
			}
		});
    	
    	crearProceso.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				new DialogoProceso(gui);
			}
		});
    	
    	crearRecurso.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				new DialogoRecurso(gui);
			}
		});
    	
    	mostrarGantt.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				gui.gantt.setVisible(true);
			}
		});
    	
    	sistema.add(iniciar);
    	sistema.add(pausar);
    	
    	procesos.add(crearProceso);
    	recursos.add(crearRecurso);
    	
    	gantt.add(mostrarGantt);
    	
    	barra.add(sistema);
    	barra.add(procesos);
    	barra.add(recursos);
    	barra.add(gantt);
    	
    	setJMenuBar(barra);
    }
    
    private void crearListener() {
    
         OS.OSEventListener console = new OS.OSEventListener() {

            @Override
            public void onRunProcess(Process pr, Processor p) {
                System.out.println(p);
            }

            @Override
            public void onEndProcess(Process pr) {
                System.out.println("ended " + pr.getName());
            }

            @Override
            public void onSuspendProcess(Process pr, Processor p) {
                System.out.println("suspendend " + pr.getName());
            }

            @Override
            public void onAddResource(Resource r) {
                System.out.println("new resource " + r);
            }

            @Override
            public void onAddProcess(Process pr, Processor p, Algorithm alg) {
                System.out.println("Added: " + pr.getName() + " to " + alg);
            }

            @Override
            public void onBlockProcess(Process pr, Processor p) {
                System.out.println("Blocked: " + pr.getName());

            }

            @Override
            public void onActiveResource(Resource r, Process pr) {
				// TODO Auto-generated method stub

            }

            @Override
            public void onFreeResource(Resource r) {
				// TODO Auto-generated method stub

            }

            @Override
            public void onBlockResource(Resource r) {
				// TODO Auto-generated method stub
            }
        };

        os.addListener(console);
        
    }
    
    public void lanzar() {
        
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                	if (running) {
                    	os.execute();
                    	actualizar();
                	}
                    
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException ex) {
                        Logger.getLogger(MultiColas.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }
        }).start();
    }
    
    public void actualizar() {
    	gantt.actualizar();
    	info.actualizar();
    	
    	for (PanelProcesador p : procesadores) {
            p.actualizar();
        }
    	
    	revalidate();
    }
    
    public void addProceso(Process pr, int indice) {
        os.getProcessors().get(indice-1).addProcess(pr);
        gantt.agregar(pr.getName());
        info.addProceso(pr);
    }
    
    public void addRecurso(String re) {
        os.addResource(re);
        info.addRecursos(os.getResources().get(re));
    }
    
    public static void main(String[] args) {
        MultiColas gui = new MultiColas(3);
        
        gui.addRecurso("Screen");
        gui.addRecurso("Printer");
        gui.addRecurso("Keyboard");
        gui.addRecurso("Mouse");
        
        /* allocate processes for processor one */
        gui.addProceso(new Process("process1", 100, new String[]{"Printer"}, 1), 1);

        gui.addProceso(new Process("process2", 2, new String[]{"Screen"}, 3), 1);
        gui.addProceso(new Process("process3", 2, new String[]{"Screen"}, 3), 1);

        gui.addProceso(new Process("process4", 2, new String[]{"Screen", "Printer"}, 3), 1);

        /* allocate processes for processor two */
        gui.addProceso(new Process("process5", 100, new String[]{}, 1), 2);

        /*gui.addProceso(new Process("process6", 3, new String[]{"Printer"}, 2), 2);
        gui.addProceso(new Process("process7", 4, new String[]{"Printer"}, 2), 2);

        gui.addProceso(new Process("process8", 2, new String[]{"Screen"}, 3), 2);*/
        
        /* allocate processes for processor three */
        gui.addProceso(new Process("process6", 100, new String[]{}, 1), 3);
        
        gui.actualizar();
        gui.lanzar();
    }
    
}

class PanelProcesador extends JPanel {
    Processor procesador;
    List<ListaAlgoritmo> algoritmos;
    
    JLabel proceso;
    JLabel recursos;
    
    PanelCola suspendidos;
    PanelCola bloqueados;
    
    public PanelProcesador(Processor p) {
        super(new GridLayout(4, 1));
        
        algoritmos = new LinkedList<>();
        
        proceso = new JLabel("-----", JLabel.CENTER);
        recursos = new JLabel("-----", JLabel.CENTER);
        
        JPanel datos = new JPanel(new GridLayout(2, 1));
        datos.add(proceso);
        datos.add(recursos);
        
        suspendidos = new PanelCola(p.getPlanner().getSuspended(), "Suspendidos");
        bloqueados = new PanelCola(p.getPlanner().getLocked(), "Bloqueados");
        procesador = p;
        
        JPanel panel = new JPanel(new GridLayout(1, p.getPlanner().getAlgorithms().size()));
        
        for (Algorithm a : procesador.getPlanner().getAlgorithms()) {
            ListaAlgoritmo la = new ListaAlgoritmo(a);
            algoritmos.add(la);
            panel.add(la);   
        }
        
        add(datos);
        add(panel);
        add(suspendidos);
        add(bloqueados);
    }
    
    public void actualizar() {
        Process current = procesador.getCurrent();
        
        if (current != null) {
            proceso.setText(String.format("N: %s       T: %s        Q: %s        P: %s", 
                    current.getName(), current.getTime(), current.getQuantum(), current.getPriority() ));
            
            recursos.setText(GUtil.stringResources(current));
            
        } else {
            proceso.setText("-----");
            recursos.setText("-----");
        }
        
        for (ListaAlgoritmo a : algoritmos) {
            a.actualizar();
        }
        
        suspendidos.actualizar();
        bloqueados.actualizar();
    }
    
}

class ListaAlgoritmo extends JPanel {
    Algorithm algoritmo;
    DefaultListModel modelo;
    
    public ListaAlgoritmo(Algorithm a) {
        super(new FlowLayout());
        
        modelo = new DefaultListModel();
        this.algoritmo = a;
        
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        
        JLabel titulo = new JLabel(a.getName());
        JList lista = new JList(modelo);
        
        c.ipady = 0;
        c.gridx = 0;
        
        c.gridy = 0;
        panel.add(titulo, c);
        
        JScrollPane scroll = new JScrollPane(lista);
        scroll.setPreferredSize(new Dimension(145, 120));
        
        c.gridy = 1;
        panel.add(scroll, c);
        
        add(panel);
    }
    
    public void actualizar() {
        Object[] array = GUtil.queueToArray(algoritmo.getProcesses());
        
        modelo.removeAllElements();
        
        if (array == null) {
            return;
        }
        
        for (Object p : array) {
            Process pr = (Process) p;
            
            modelo.addElement(pr.getName());
        }
    }
}

class PanelCola extends JPanel {

    Queue<Process> procesos;
    DefaultListModel<String> modelo;
    
    public PanelCola(Queue<Process> procesos, String titulo) {
        super(new FlowLayout());
        
        modelo = new DefaultListModel<>();
        this.procesos = procesos;
        
        JList lista = new JList(modelo);
        JScrollPane scroll = new JScrollPane(lista);
        scroll.setPreferredSize(new Dimension(400, 150));
        
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        
        c.ipady = 0;
        c.gridx = 0;
        
        c.gridy = 0;
        panel.add(new JLabel(titulo), c);
        
        c.gridy = 1;
        panel.add(scroll, c);
        
        add(panel);
    }
    
    public void actualizar() {
        Object[] array = GUtil.queueToArray(procesos);
        
        modelo.removeAllElements();
        
        if (array == null) {
            return;
        }
        
        for (Object p : array) {
            Process pr = (Process) p;
            
            modelo.addElement(pr.getName());
        }
    }
}

abstract class Dialogo extends JFrame {
	
	MultiColas gui;
	
	public Dialogo(String titulo, MultiColas gui) {
		super(titulo);
		this.gui = gui;
		
		setPreferredSize(build());
		
		pack();
		setVisible(true);
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
	}
	
	protected abstract Dimension build();
	
}

class DialogoProceso extends Dialogo {
	
	JTextField nombre;
	JTextField tiempo;
	JTextField prioridad;
	JList<String> recursos;
	JComboBox<String> procesador;
	
	public DialogoProceso(MultiColas gui) {
		super("Crear Proceso", gui);
	}
	
	protected Dimension build() {
		nombre = new JTextField();
		tiempo = new JTextField();
		prioridad = new JTextField();
		recursos = new JList<>();
		procesador = new JComboBox<>();
		
		for (Processor p : gui.os.getProcessors()) {
			procesador.addItem(p.getName());
		}
		procesador.setSelectedIndex(0);
		
		setLayout(new GridLayout(1, 3));
		
		JPanel panel = new JPanel();
		panel.setLayout(new GridLayout(4, 2));
		
		panel.add(new Label("Nombre"));
		panel.add(nombre);
		
		panel.add(new Label("Tiempo"));
		panel.add(tiempo);
		
		panel.add(new Label("Prioridad"));
		panel.add(prioridad);
		
		panel.add(new Label("Procesador"));
		panel.add(procesador);
		
		add(panel);
		
		/* Create resource list */
		DefaultListModel<String> modelo = new DefaultListModel<>();
		recursos.setModel(modelo);
		
		for (String res : gui.os.getResources().keySet()) {
			modelo.addElement(res);
		}
		
		JPanel panelRes = new JPanel(new GridLayout(2, 1));
		
		panelRes.add(new Label("Recursos"));
		panelRes.add(new JScrollPane(recursos));
		
		add(panelRes);
		
		JButton crear = new JButton("Crear");
		crear.setPreferredSize(new Dimension(20,20));
		crear.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				
				List<String> listado = recursos.getSelectedValuesList();
				String[] recursos = new String[listado.size()]; 
				
				for (int i=0; i<recursos.length; i++) {
					recursos[i] = listado.get(i);
					System.out.println(recursos[i]);
				}
				
				gui.addProceso(new Process(nombre.getText(), 
								Integer.parseInt(tiempo.getText()), 
								recursos, 
								Integer.parseInt(prioridad.getText())), 
							procesador.getSelectedIndex()+1);
				
				gui.actualizar();
				dispose();
			}
		});

		add(crear);
		
		return new Dimension(700, 150);
	}	
}

class DialogoRecurso extends Dialogo {

	JTextField nombre;

	public DialogoRecurso(MultiColas gui) {
		super("Crear Recurso", gui);
	}
	
	@Override
	protected Dimension build() {
		nombre = new JTextField();
		
		setLayout(new GridLayout(2, 2));
		
		add(new JLabel("Nombre"));
		add(nombre);
		
		JButton crear = new JButton("Crear");
		crear.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				gui.addRecurso(nombre.getText());
				gui.actualizar();
				dispose();
			}
		});
		
		add(new JLabel());
		add(crear);
		
		return new Dimension(500, 100);
	}	
}

class DiagramaGantt extends JFrame {
	
	List<Process> procesos;
	DefaultTableModel modelo;
	JTable tabla;
	JScrollPane scroll;
	
	public DiagramaGantt(List<Process> procesos) {
		super("Diagrama Gantt");
		
		modelo = new DefaultTableModel();
		tabla = new JTable(modelo);
		this.procesos = procesos;
		
		scroll = new JScrollPane(tabla);
		tabla.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		add(scroll);
		
		modelo.addColumn("Nombre");
		
		setDefaultCloseOperation(HIDE_ON_CLOSE);
		pack();
	}
	
	private void scrolling() {
		int columna = tabla.getColumnCount()-1;
		int fila = tabla.getRowCount()- 1;
		
		tabla.scrollRectToVisible(tabla.getCellRect(fila, columna, true));
		tabla.clearSelection();
		tabla.setColumnSelectionInterval(columna, columna);
		modelo.fireTableDataChanged();
	}
	
	public void agregar(String proceso) {
		modelo.addRow(new String[modelo.getColumnCount()]);
		modelo.setValueAt(proceso, modelo.getRowCount()-1, 0);
		
		scrolling();
	}
	
	public void actualizar() {
		
		modelo.addColumn("T " + modelo.getColumnCount());
		
		int contador = 0;
		
		for (Process pr : procesos) {
			modelo.setValueAt(pr.getState().toString(), contador, modelo.getColumnCount()-1);
			contador += 1; 
		}
		
		scrolling();
	}
}

class OSInfo extends JPanel {
	
	List<Process> procesos;
	HashMap<String, Resource> recursos;
	
	DefaultListModel<String> mProcesos;
	DefaultListModel<String> mRecursos;
	
	public OSInfo(OS os) {
		super(new GridBagLayout());
		
		procesos = os.getProcesses();
		recursos = os.getResources();
		
		mProcesos = new DefaultListModel<>();
		mRecursos = new DefaultListModel<>();
		
		JList lProcesos = new JList(mProcesos);
		JList lRecursos = new JList(mRecursos);
		
		GridBagConstraints c = new GridBagConstraints();
		c.gridx = 0;
		c.fill = c.HORIZONTAL;
		
		c.gridy = 0;
		c.ipady = 0;
		add(new JLabel("Procesos"), c);
		
		c.gridy = 1;
		c.ipady = 100;
		add(new JScrollPane(lProcesos),c);
		
		c.gridy = 2;
		c.ipady = 0;
		add(new JLabel("Recursos"), c);
		
		c.gridy = 3;
		c.ipady = 100;
		add(new JScrollPane(lRecursos), c);
	}
	
	public void actualizar() {
		mProcesos.removeAllElements();
		mRecursos.removeAllElements();
		
		for (Process pr : procesos) {
			addProceso(pr);
		}
		
		for (Resource re : recursos.values()) {
			addRecursos(re);
		}
		
		revalidate();
	}
	
	public void addProceso(Process pr) {
		mProcesos.addElement("N:  "+pr.getName() + "    T:  " + pr.getTime() + "    S:  " + pr.getState());
	}
	
	public void addRecursos(Resource re) {
		Process pr = re.getProcess();
		String proceso = pr == null ? "-----" : pr.getName();
		
		mRecursos.addElement("N:  "+re.getName() + "       P: " + proceso);
	}
}
