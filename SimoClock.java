
// WELCOME TO SIMOCLOCK THE DIGITAL CLOCK NO ONE EVER NEEDED BUT IT'S HERE ANYWAY!

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.Locale;
import java.util.prefs.Preferences;
import javax.swing.JButton;
import java.awt.Color;

public class SimoClock {
	private JFrame frame;
	private JLabel timeLabel;
	private JLabel dateLabel;
	private JComboBox<String> fontComboBox;
	private JButton foregroundButton;
	private JButton secButton;
	private JButton dateButton;
	private JButton saveColorButton;
	private JButton altDateButton;
	private JButton cfgButton;
	private JButton colorsButton;
	private boolean foregroundMode;
	private boolean showSeconds;
	private boolean showDate;
	private boolean altDateMode;
	private Color fontColor;
	private Color activeColor = new Color(0, 255, 255);
	private Color inactiveColor = new Color(0, 0, 0);//UIManager.getColor("Button.background")
	private Preferences preferences;
	private JButton fsButton;
	private boolean fullscreenMode;
	private JButton boldButton;
	private boolean boldMode;
	private boolean showConfig = false; // Stato variabile visualizzazione pulsanti all avvio
	private JButton fontButton; // Pulsante opzionale per visualizzare il menu dei font

    public SimoClock() {
        // Inizializzazione delle preferenze
        preferences = Preferences.userRoot().node(this.getClass().getName());
		
        // Creazione della finestra JFrame
        frame = new JFrame("SimoClock - The Unnecessary Digital Clock!");

        // Creazione delle etichette per l'ora e la data
        timeLabel = new JLabel();
        timeLabel.setHorizontalAlignment(SwingConstants.CENTER);
        dateLabel = new JLabel();
        dateLabel.setVisible(false);
        dateLabel.setHorizontalAlignment(SwingConstants.CENTER);

        // Creazione della JComboBox per la selezione del font
        fontComboBox = new JComboBox<>(GraphicsEnvironment.getLocalGraphicsEnvironment().getAvailableFontFamilyNames());

        // Creazione dei pulsanti
        cfgButton = new JButton("");
        foregroundButton = new JButton("Fg");
        secButton = new JButton("Sec");
        dateButton = new JButton("Date");
        saveColorButton = new JButton("Write");
        altDateButton = new JButton("Alt");
        fsButton = new JButton("Fs");
		colorsButton = new JButton("Clr");
        colorsButton.addActionListener(e -> showColorsMenu(colorsButton));
        boldButton = new JButton("Bold");
        fontButton = new JButton("@"); // Pulsante opzionale per visualizzare il menu dei font
		fontButton.addActionListener(e -> showFontMenu());

		// Colori sfondo pulsanti statici - RGB values
		colorsButton.setBackground(new Color(200, 200, 0));
		fontComboBox.setBackground(new Color(0, 180, 60));
		fontButton.setBackground(new Color(0, 180, 60)); // Colore pulsante opzionale menù font
		saveColorButton.setBackground(new Color(230, 0, 0));

        // Impostazione del layout della finestra
        frame.setLayout(new GridBagLayout());
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.gridx = 0;
        constraints.gridy = 0;
        constraints.weightx = 1.0;
        constraints.weighty = 1.0;
        constraints.fill = GridBagConstraints.BOTH;

        // Aggiunta delle etichette alla finestra
        frame.getContentPane().add(timeLabel, constraints);
        constraints.gridy = 1;
        frame.getContentPane().add(dateLabel, constraints);

        // Impostazioni della finestra
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(766, 426);

        // Gestione della JComboBox per la selezione del font
        fontComboBox.addActionListener(e -> updateFont());

        // Gestione dei pulsanti
        foregroundButton.addActionListener(e -> toggleForegroundMode());
        secButton.addActionListener(e -> toggleShowSeconds());
        dateButton.addActionListener(e -> toggleShowDate());
        saveColorButton.addActionListener(e -> saveColors());
        altDateButton.addActionListener(e -> toggleAltDateMode());
        fsButton.addActionListener(e -> toggleFullscreenMode());
		
		// Messaggio pulsante salva cfg
		saveColorButton.addActionListener(e -> {
            JOptionPane.showMessageDialog(frame, "Preferences Saved Successfully!");
        });

        // Creazione del pannello dei pulsanti
        JPanel buttonPanel = new JPanel();
		buttonPanel.setOpaque(false); // Imposta il pannello dei pulsanti trasparente
		buttonPanel.add(fsButton);
        buttonPanel.add(foregroundButton);
        buttonPanel.add(secButton);
        buttonPanel.add(dateButton);
		buttonPanel.add(altDateButton);
		buttonPanel.add(boldButton);
		buttonPanel.add(cfgButton);
        buttonPanel.add(fontComboBox);
		buttonPanel.add(fontButton);
        buttonPanel.add(colorsButton);
        buttonPanel.add(saveColorButton);
	   
        // Aggiunta del pannello dei pulsanti alla finestra
        constraints.gridy = 2;
        constraints.weighty = 0.0;
        constraints.insets = new Insets(20, 0, 8, 0); // Posizione bordi finestra pulsanti
        frame.getContentPane().add(buttonPanel, constraints);

        // Aggiunta di un listener per adattare la dimensione del font in base alla dimensione della finestra
        frame.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                adjustFontSize();
            }
        });

		boldButton.addActionListener(e -> toggleBoldMode());

        // Impostazioni finali della finestra
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);

        // Caricamento delle impostazioni salvate
        loadColors();

        // Avvio dell'aggiornamento dell'orologio
        updateClock();

        // Adattamento del font alla dimensione della finestra
        adjustFontSize();
    }

    // Metodo per l'aggiornamento dell'orologio
    private void updateClock() {
        Timer timer = new Timer(1000, e -> {
            SimpleDateFormat timeFormat;
            SimpleDateFormat dateFormat;
            if (showSeconds) {
                timeFormat = new SimpleDateFormat("HH:mm:ss");
            } else {
                timeFormat = new SimpleDateFormat("HH:mm");
            }
            dateFormat = altDateMode ? new SimpleDateFormat("EEEE dd MMMM", Locale.ENGLISH) : new SimpleDateFormat("EEEE dd/MM/yyyy", Locale.ITALIAN); //FORMATO LINGUA DATA
            String currentTime = timeFormat.format(new Date());
            String currentDate = dateFormat.format(new Date());
            timeLabel.setText(currentTime);
            dateLabel.setText(currentDate);
		// Aggiunta della logica per impostare lo stile grassetto del font
        Font currentFont = timeLabel.getFont();
        Font dateFont = dateLabel.getFont();
        if (boldMode) {
            Font boldFont = new Font(currentFont.getName(), Font.BOLD, currentFont.getSize());
            timeLabel.setFont(boldFont);
            float fontSize = currentFont.getSize() * 0.37f; // Calcolo della dimensione proporzionale per il font della data
            Font boldDateFont = new Font(dateFont.getName(), Font.BOLD, (int) fontSize);
            dateLabel.setFont(boldDateFont);
        } else {
            Font plainFont = new Font(currentFont.getName(), Font.PLAIN, currentFont.getSize());
            timeLabel.setFont(plainFont);
            float fontSize = currentFont.getSize() * 0.37f; // Calcolo della dimensione proporzionale per il font della data
            Font plainDateFont = new Font(dateFont.getName(), Font.PLAIN, (int) fontSize);
            dateLabel.setFont(plainDateFont);
        }
        });
        timer.start();
    }

    // Metodo per adattare la dimensione del font alla dimensione della finestra
    private void adjustFontSize() {
        int fontSize = Math.max(frame.getWidth() / 5, 0); // DIMENSIONE FONT ORA
        timeLabel.setFont(new Font(timeLabel.getFont().getName(), Font.PLAIN, fontSize)); //STILE FONT ORA
        updateDateLabelFontSize();
    }

    // Metodo per adattare la dimensione del font dell'etichetta della data
    private void updateDateLabelFontSize() {
        Font timeFont = timeLabel.getFont();
        int dateFontSize = (int) (timeFont.getSize() * 0.37); //RATIO ORA-DATA
        dateLabel.setFont(new Font(timeFont.getName(), Font.PLAIN, dateFontSize)); //STILE FONT DATA
    }

    // Metodo per mostrare il menu dei colori
    private void showColorsMenu(Component parent) {
        JPopupMenu colorsMenu = new JPopupMenu();
        JMenuItem backgroundColorItem = new JMenuItem("Background");//Background Color
        backgroundColorItem.addActionListener(e -> selectBackgroundColor());
        colorsMenu.add(backgroundColorItem);
        JMenuItem fontColorItem = new JMenuItem("Font");//Font Color
        fontColorItem.addActionListener(e -> selectFontColor());
        colorsMenu.add(fontColorItem);
        colorsMenu.show(parent, 0, parent.getHeight());
    }

    // Metodo per selezionare il colore di sfondo
    private void selectBackgroundColor() {
        Color selectedColor = JColorChooser.showDialog(frame, "Select Background Color", frame.getContentPane().getBackground());//Select Background Color
        if (selectedColor != null) {
            frame.getContentPane().setBackground(selectedColor);
        }
    }

    // Metodo per selezionare il colore del font
    private void selectFontColor() {
        Color selectedColor = JColorChooser.showDialog(frame, "Select Font Color", fontColor);//Select Font Color
        if (selectedColor != null) {
            fontColor = selectedColor;
            timeLabel.setForeground(fontColor);
            dateLabel.setForeground(fontColor);
        }
    }

    // Metodo per aggiornare il font dell'orologio
    private void updateFont() {
        String selectedFontName = (String) fontComboBox.getSelectedItem();
        Font currentFont = timeLabel.getFont();
        Font selectedFont = new Font(selectedFontName, currentFont.getStyle(), currentFont.getSize());
        timeLabel.setFont(selectedFont);
        updateDateLabelFontSize();
        adjustFontSize();
    }

    // Metodo per attivare/disattivare la modalità foreground
    private void toggleForegroundMode() {
        foregroundMode = !foregroundMode;
        frame.setAlwaysOnTop(foregroundMode);
        foregroundButton.setBackground(foregroundMode ? activeColor : inactiveColor);
    }

    // Metodo per attivare/disattivare la visualizzazione dei secondi
    private void toggleShowSeconds() {
        showSeconds = !showSeconds;
        secButton.setBackground(showSeconds ? activeColor : inactiveColor);
        //saveShowSecondsState(); //Decommentare per attivare salvataggio stato in chiusura app
    }

    // Metodo per attivare/disattivare la visualizzazione della data
    private void toggleShowDate() {
        showDate = !showDate;
        dateLabel.setVisible(showDate);
        dateButton.setBackground(showDate ? activeColor : inactiveColor);
        //saveShowDateState(); //Decommentare per attivare salvataggio stato in chiusura app
    }

    // Metodo per attivare/disattivare la visualizzazione della data alternativa
    private void toggleAltDateMode() {
        altDateMode = !altDateMode;
        altDateButton.setBackground(altDateMode ? activeColor : inactiveColor);
        //saveAltDateMode(); //Decommentare per attivare salvataggio stato in chiusura app
        updateClock();
    }

    // Metodo per salvare le impostazioni dei colori, del font e delle opzioni di visualizzazione
    private void saveColors() {
        saveFontColor();
        saveBackgroundColor();
        saveFont();
        saveShowSecondsState();
        saveShowDateState();
        saveAltDateMode();
		saveBoldMode();
	}

    // Metodo per salvare il colore del font
    private void saveFontColor() {
        preferences.putInt("fontColorRed", fontColor.getRed());
        preferences.putInt("fontColorGreen", fontColor.getGreen());
        preferences.putInt("fontColorBlue", fontColor.getBlue());
    }

    // Metodo per salvare il colore di sfondo
    private void saveBackgroundColor() {
        Color backgroundColor = frame.getContentPane().getBackground();
        preferences.putInt("backgroundColorRed", backgroundColor.getRed());
        preferences.putInt("backgroundColorGreen", backgroundColor.getGreen());
        preferences.putInt("backgroundColorBlue", backgroundColor.getBlue());
    }

    // Metodo per salvare il font
    private void saveFont() {
        Font currentFont = timeLabel.getFont();
        preferences.put("fontName", currentFont.getName());
        preferences.putInt("fontStyle", currentFont.getStyle());
        preferences.putInt("fontSize", currentFont.getSize());
    }

    // Metodo per salvare lo stato di visualizzazione dei secondi
    private void saveShowSecondsState() {
        preferences.putBoolean("showSeconds", showSeconds);
    }

    // Metodo per salvare lo stato di visualizzazione della data
    private void saveShowDateState() {
        preferences.putBoolean("showDate", showDate);
    }

    // Metodo per salvare lo stato della modalità data alternativa
    private void saveAltDateMode() {
        preferences.putBoolean("altDateMode", altDateMode);
    }

	// Metodo per salvare lo stato dello stile grassetto
    private void saveBoldMode() {
        preferences.putBoolean("boldMode", boldMode);
    }

	// Metodo per caricare le impostazioni dei colori, del font e delle opzioni di visualizzazione
    private void loadColors() {
        int fontColorRed = preferences.getInt("fontColorRed", 0);
        int fontColorGreen = preferences.getInt("fontColorGreen", 100);
        int fontColorBlue = preferences.getInt("fontColorBlue", 100);
        fontColor = new Color(fontColorRed, fontColorGreen, fontColorBlue);
        timeLabel.setForeground(fontColor);
        dateLabel.setForeground(fontColor);

        int backgroundColorRed = preferences.getInt("backgroundColorRed", 0);
        int backgroundColorGreen = preferences.getInt("backgroundColorGreen", 0);
        int backgroundColorBlue = preferences.getInt("backgroundColorBlue", 0);
        Color backgroundColor = new Color(backgroundColorRed, backgroundColorGreen, backgroundColorBlue);
        frame.getContentPane().setBackground(backgroundColor);

        String fontName = preferences.get("fontName", "Agency FB");
        int fontStyle = preferences.getInt("fontStyle", Font.PLAIN);
        int fontSize = preferences.getInt("fontSize", 20);
        Font savedFont = new Font(fontName, fontStyle, fontSize);
        timeLabel.setFont(savedFont);
        updateDateLabelFontSize();
        fontComboBox.setSelectedItem(fontName);

        showSeconds = preferences.getBoolean("showSeconds", false);
        secButton.setBackground(showSeconds ? activeColor : inactiveColor);

        showDate = preferences.getBoolean("showDate", false);
        dateLabel.setVisible(showDate);
        dateButton.setBackground(showDate ? activeColor : inactiveColor);
		
		boldMode = preferences.getBoolean("boldMode", false);
		boldButton.setBackground(boldMode ? activeColor : inactiveColor);
		
		fullscreenMode = preferences.getBoolean("fullscreenMode", false);
		fsButton.setBackground(fullscreenMode ? activeColor : inactiveColor);
		
		foregroundMode = preferences.getBoolean("foregroundMode", false);
		foregroundButton.setBackground(foregroundMode ? activeColor : inactiveColor);
		
		altDateMode = preferences.getBoolean("altDateMode", false);
        altDateButton.setBackground(altDateMode ? activeColor : inactiveColor);
	}
	
	private void toggleFullscreenMode() {
    fullscreenMode = !fullscreenMode;
    GraphicsEnvironment env = GraphicsEnvironment.getLocalGraphicsEnvironment();
    GraphicsDevice device = env.getDefaultScreenDevice();
    if (fullscreenMode) {
        frame.dispose();
        frame.setUndecorated(true);
        frame.setVisible(true);
        device.setFullScreenWindow(frame);
        fsButton.setBackground(activeColor);
    } else {
        device.setFullScreenWindow(null);
        frame.dispose();
        frame.setUndecorated(false);
        frame.setVisible(true);
        fsButton.setBackground(inactiveColor);
		}
	}

	private void toggleBoldMode() {
        boldMode = !boldMode;
        if (boldMode) {
            boldButton.setBackground(activeColor);
        } else {
            boldButton.setBackground(inactiveColor);

        }
    }

	// Aggiungi il metodo per visualizzare il menu dei font
    private void showFontMenu() {
        String[] fonts = GraphicsEnvironment.getLocalGraphicsEnvironment().getAvailableFontFamilyNames();
        String selectedFont = (String) JOptionPane.showInputDialog(frame, "SELECT:", "[FONTS]", JOptionPane.PLAIN_MESSAGE, null, fonts, fonts[0]);
        if (selectedFont != null) {
            Font currentFont = timeLabel.getFont();
            Font selectedFontObj = new Font(selectedFont, currentFont.getStyle(), currentFont.getSize());
            timeLabel.setFont(selectedFontObj);
            updateDateLabelFontSize();
            adjustFontSize();
        }
    }
	private void toggleShowConfig() {
        showConfig = !showConfig;
        if (showConfig) {
			foregroundButton.setVisible(false);
			secButton.setVisible(false);
			dateButton.setVisible(false);
			fontComboBox.setVisible(false);
			fontButton.setVisible(false);
			fsButton.setVisible(false);
			boldButton.setVisible(false);
			altDateButton.setVisible(false);
			saveColorButton.setVisible(false);
			colorsButton.setVisible(false);
			cfgButton.setBackground(inactiveColor);
           } else {
			foregroundButton.setVisible(true); // Pulsante primo piano
			secButton.setVisible(true); // Pulsante secondi
			dateButton.setVisible(true); // Pulsante data
			fontComboBox.setVisible(true); // Visualizzare il menù a tendina font
			//fontButton.setVisible(true); // Visualizzare il pulsante menù font "@" inattivo
			fsButton.setVisible(true); // Pulsante fullscreen
			boldButton.setVisible(true); // Pulsante bold
			altDateButton.setVisible(true); // Pulsante alt data
			saveColorButton.setVisible(true); // Pulsante savecfg
			colorsButton.setVisible(true); // Color menù
			cfgButton.setBackground(activeColor);
        }
	}	
	
	public static void main(String[] args) {
		SwingUtilities.invokeLater(() -> {
        SimoClock simoClock = new SimoClock();
        
        // Aggiungi un ActionListener al pulsante cfg per gestirne gli eventi
        simoClock.cfgButton.addActionListener(e -> {
            simoClock.toggleShowConfig(); // Chiama il metodo toggleShowConfig() quando il pulsante viene premuto
        });
        
        // Imposta lo stato iniziale di showConfig e la visibilità dei pulsanti associati
        simoClock.toggleShowConfig();
		});
	}

}
