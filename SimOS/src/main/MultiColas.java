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
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;

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
    
    public MultiColas(int cantidad) {
        super("MultiColas");
        
        os = new OS();
        procesadores = new LinkedList<>();
        crearBarraMenu();
        
        JPanel panel = new JPanel(new GridLayout(1, cantidad));
        
        for (int i=0; i<cantidad; i++) {
            Planner pl = new MultiLevel();
            Processor p = new Processor("Processor"+(i+1), pl);
            
            pl.addAlgorithm(new RoundRobin());
            pl.addAlgorithm(new SRJF());
            pl.addAlgorithm(new Algorithm());
            
            os.addProcessor(p);
            
            PanelProcesador pPanel = new PanelProcesador(p);
            procesadores.add(pPanel);
            panel.add(pPanel);
        }
        
        add(panel);
        
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(350 * cantidad, 600);
        setVisible(true);
        
        crearLogger();
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
    	
    	iniciar.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				
			}
		});
    	
    	pausar.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				
			}
		});
    	
    	crearProceso.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				new DialogoProceso();
			}
		});
    	
    	crearRecurso.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				new DialogoRecurso();
			}
		});
    	
    	mostrarGantt.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				
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
    
    private void crearLogger() {
    
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
                    actualizar();
                    os.execute();
                    
                    try {
                        Thread.sleep(2000);
                    } catch (InterruptedException ex) {
                        Logger.getLogger(MultiColas.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }
        }).start();
    }
    
    public void actualizar() {
        for (PanelProcesador p : procesadores) {
            p.actualizar();
        }
    }
    
    public void addProceso(Process p, int indice) {
        os.getProcessors().get(indice-1).addProcess(p);
    }
    
    public void addRecurso(String res) {
        os.addResource(res);
    }
    
    public static void main(String[] args) {
        MultiColas gui = new MultiColas(2);
        
        gui.addRecurso("Screen");
        gui.addRecurso("Printer");
        
        /* allocate processes for processor one */
        gui.addProceso(new Process("process1", 5, new String[]{"Printer"}, 1), 1);

        gui.addProceso(new Process("process2", 3, new String[]{"Screen"}, 2), 1);
        gui.addProceso(new Process("process3", 4, new String[]{"Screen"}, 2), 1);

        gui.addProceso(new Process("process4", 2, new String[]{"Screen", "Printer"}, 3), 1);

        /* allocate processes for processor two */
        gui.addProceso(new Process("process5", 5, new String[]{"Screen"}, 1), 2);

        gui.addProceso(new Process("process6", 3, new String[]{"Printer"}, 2), 2);
        gui.addProceso(new Process("process7", 4, new String[]{"Printer"}, 2), 2);

        gui.addProceso(new Process("process8", 2, new String[]{"Screen"}, 3), 2);
        
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
        
        proceso = new JLabel("---");
        recursos = new JLabel("---");
        
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
            proceso.setText(String.format("     N: %s       T: %s        Q: %s        P: %s", 
                    current.getName(), current.getTime(), current.getQuantum(), current.getPriority() ));
            
            recursos.setText("     "+GUtil.stringResources(current));
            
        } else {
            proceso.setText("     -----");
            recursos.setText("     -----");
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
	
	public Dialogo() {
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
	
	protected Dimension build() {
		nombre = new JTextField();
		tiempo = new JTextField();
		prioridad = new JTextField();
		recursos = new JList<>();
		
		
		setLayout(new GridLayout(5, 2));
		
		add(new Label("Nombre"));
		add(nombre);
		
		add(new Label("Tiempo"));
		add(tiempo);
		
		add(new Label("Prioridad"));
		add(prioridad);
		
		add(new Label("Recursos"));
		add(new JScrollPane(recursos));
		
		JButton crear = new JButton("Crear");
		crear.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				
			}
		});
		
		add(new JLabel());
		add(crear);
		
		return new Dimension(500, 150);
	}	
}

class DialogoRecurso extends Dialogo {

	JTextField nombre;

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
				
			}
		});
		
		add(new JLabel());
		add(crear);
		
		return new Dimension(500, 100);
	}
	
}
