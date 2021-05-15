package main.view;

import javafx.event.EventHandler;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.VPos;
import javafx.scene.Scene;
import javafx.scene.SnapshotParameters;
import javafx.scene.control.*;
import javafx.scene.effect.DropShadow;
import javafx.scene.effect.Effect;
import javafx.scene.image.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import main.project_enums.BubbleType;
import main.project_enums.Selected;
import java.io.File;
import java.util.ArrayList;

//main.view.AppGUI.java represents the View following the MVC pattern
public class AppGUI implements ViewThemeColors
{
    private static final double SCENE_WIDTH = 1300;
    private static final double SCENE_HEIGHT = 850;
    private final double WORKING_PANE_WIDTH = 610;
    private final double WORKING_PANE_HEIGHT = 600;
    private final double BUBBLE_WIDTH = 290;
    private final double BUBBLE_HEIGHT = 220;
    private final double CHARACTER_VIEW_SIZE = 300;
    private final double COMIX_STRIP_PANE_HEIGHT = 160;
    private final double PANEL_SIZE = COMIX_STRIP_PANE_HEIGHT - 10;
    private final double LEFT_BUTTONS_PANEL_WIDTH = 200;
    private final Image THOUGHT_BUBBLE_IMAGE = new Image("/resources/thoughtBubble.png");
    private final Image SPEECH_BUBBLE_IMAGE = new Image("/resources/speechBubble.png");
    private File defaultCharactersDirectory = new File("./");
    private File defaultHTMLDirectory = new File("./");

    private Stage stage;
    private Scene scene;
    private BorderPane layout;
    private GridPane mainPane = new GridPane();
    private ImageView leftBubble = new ImageView();
    private ImageView rightBubble = new ImageView();
    private ImageView leftCharView = new ImageView();
    private ImageView rightCharView = new ImageView();
    private Text leftBubbleText = new Text();
    private Text rightBubbleText = new Text();
    private Text topNarrativeText = new Text();
    private Text bottomNarrativeText = new Text();
    private ImageView selectedCharacterView = null;
    private Color selectedColor = Color.WHITE;
    private HBox comixStrip;
    private PanelView selectedPanel;
    private HelpPage helpPageClass;
    private Label panelPosition;

    //SIDE BUTTONS
    private ColorPicker colorPalette;
    private Button importLeftCharButton;
    private Button importRightCharButton;
    private Button flipButton;
    private Button genderSwapButton;
    private Button changeSkinToneButton;
    private Button changeHairColorButton;
    private Button changeLipsColorButton;
    private Button addSpeechBubbleButton;
    private Button addThoughtBubbleButton;
    private Button removeBubbleButton;
    private Button addTextTopButton;
    private Button addTextBottomButton;
    private Button setComicTitle;
    private Button setComicCredits;

    //TOP BAR MENU BUTTONS
    private Menu fileMenu;
    private Menu panelMenu;
    private Menu helpMenu;
    private Menu messageMenu;

    //MENU OPTIONS
    private final MenuItem fileMenuSaveXML = new MenuItem("Save");
    private final MenuItem fileMenuLoadXML = new MenuItem("Load");
    private final MenuItem fileMenuCharactersDir = new MenuItem("Characters Directory");
    private final MenuItem saveAsHtml = new MenuItem("Save as HTML");
    private final MenuItem panelMenuNew = new MenuItem("New");
    private final MenuItem panelMenuSave = new MenuItem("Save");
    private final MenuItem panelMenuDelete = new MenuItem("Delete");
    private final MenuItem help = new MenuItem("Help");
    private final MenuItem about = new MenuItem("About");
    private final MenuItem gettingStarted = new MenuItem("Getting Started");


    //SELECTED PANEL CONTEXT MENU
    private final ContextMenu SELECTED_PANEL_MENU = new ContextMenu();

    //SELECTED PANEL CONTEXT MENU ITEMS
    private final MenuItem SAVE_PANEL = new MenuItem("Save");
    private final MenuItem DELETE_PANEL = new MenuItem("Delete");
    private final MenuItem CHANGE_PANEL_POSITION = new MenuItem("Change panel position");

    //NARRATIVE TEXT CONTEXT MENU (right click menu)
    private final ContextMenu TOP_NARRATIVE_TEXT_MENU = new ContextMenu();
    private final ContextMenu BOTTOM_NARRATIVE_TEXT_MENU = new ContextMenu();

    //NARRATIVE TEXT CONTEXT MENU OPTIONS
    private final MenuItem SINGLE_LINE_OPTION_TOP = new MenuItem("Single line");
    private final MenuItem MULTI_LINES_OPTION_TOP = new MenuItem("Wrap text");
    private final MenuItem SINGLE_LINE_OPTION_BOTTOM = new MenuItem("Single line");
    private final MenuItem MULTI_LINES_OPTION_BOTTOM = new MenuItem("Wrap text");

    public AppGUI(Stage stage){
        this.stage = stage;
    }

    public void createUI()
    {
        layout = new BorderPane();
        layout.setStyle("-fx-background-color: " + APP_THEME_COLOR_SCENE);
        createTopMenuBar();
        createButtons();
        createMainPane();
        createBottomPane();
        //creates the 'right click' menus
        createContextMenu();
        scene = new Scene(layout);
        stage.setScene(scene);
        stage.setWidth(SCENE_WIDTH);
        //stage.setHeight(SCENE_HEIGHT);
        //stage.setMaximized(true);
        stage.setTitle("HomiesComix");
        stage.show();
    }

    public void createMainPane()
    {
        leftBubble.setFitHeight(BUBBLE_HEIGHT);
        leftBubble.setFitWidth(BUBBLE_WIDTH);

        rightBubble.setFitHeight(BUBBLE_HEIGHT);
        rightBubble.setFitWidth(BUBBLE_WIDTH);

        leftCharView.setFitHeight(CHARACTER_VIEW_SIZE);
        leftCharView.setFitWidth(CHARACTER_VIEW_SIZE);
        leftCharView.setPreserveRatio(true);
        leftCharView.setStyle("-fx-cursor: hand");

        rightCharView.setFitHeight(CHARACTER_VIEW_SIZE);
        rightCharView.setFitWidth(CHARACTER_VIEW_SIZE);
        rightCharView.setPreserveRatio(true);
        rightCharView.setStyle("-fx-cursor: hand");

        //Stack pane wrapper for the bubble and text
        StackPane leftBubbleWrapper = new StackPane(leftBubble, leftBubbleText);
        StackPane rightBubbleWrapper = new StackPane(rightBubble, rightBubbleText);

        leftBubbleWrapper.setMaxSize(300, 220);
        leftBubbleWrapper.setAlignment(Pos.TOP_CENTER);
        rightBubbleWrapper.setMaxSize(300, 220);
        rightBubbleWrapper.setAlignment(Pos.TOP_CENTER);

        StackPane.setMargin(leftBubbleText, new Insets(15, 0, 0,0));
        StackPane.setMargin(rightBubbleText, new Insets(15, 0, 0,0));

        bubbleTextStyle(leftBubbleText);
        bubbleTextStyle(rightBubbleText);

        //size for each row and col
        //first & last row: height= 40 & width= gridspan
        //second row height 220 and width 300
        //third row size is 300 x 300
        RowConstraints row0 = new RowConstraints();
        row0.setMaxHeight(40);
        row0.setMinHeight(40);
        RowConstraints row1 = new RowConstraints();
        row1.setPrefHeight(BUBBLE_HEIGHT);
        RowConstraints row2 = new RowConstraints();
        row2.setPrefHeight(CHARACTER_VIEW_SIZE);
        RowConstraints row3 = new RowConstraints();
        row3.setMaxHeight(40);
        row3.setMinHeight(40);
        mainPane.getRowConstraints().addAll(row0, row1, row2, row3);

        //(Node, colIndex, rowIndex, colSpan, rowSpan)
        mainPane.add(topNarrativeText, 0, 0, 2, 1);
        mainPane.add(leftBubbleWrapper, 0,1,1,1);
        mainPane.add(rightBubbleWrapper, 1,1,1,1);
        mainPane.add(leftCharView, 0, 2, 1, 1);
        mainPane.add(rightCharView, 1, 2, 1, 1);
        mainPane.add(bottomNarrativeText, 0, 3, 2, 1);

        GridPane.setValignment(leftBubbleWrapper, VPos.BOTTOM);
        GridPane.setValignment(rightBubbleWrapper, VPos.BOTTOM);
        GridPane.setHalignment(topNarrativeText, HPos.CENTER);
        GridPane.setHalignment(bottomNarrativeText, HPos.CENTER);

        mainPane.setStyle("-fx-background-color: white");
        mainPane.setMinSize(WORKING_PANE_WIDTH, WORKING_PANE_HEIGHT);
        mainPane.setMaxSize(WORKING_PANE_WIDTH, WORKING_PANE_HEIGHT);
        mainPane.setHgap(10);
        //mainPane.setGridLinesVisible(true);

        BorderPane.setAlignment(mainPane, Pos.CENTER);
        BorderPane.setMargin(mainPane, new Insets(10, 10, 10, 10));
        layout.setCenter(mainPane);
    }
    public void createBottomPane()
    {
        //Hbox containing the panels(the comic strip)
        comixStrip = new HBox();
        comixStrip.setSpacing(15);
        comixStrip.setPadding(new Insets(5, 5, 5, 5));
        comixStrip.setStyle("-fx-background-color: " + APP_THEME_COLOR + ";");
        comixStrip.setMinHeight(COMIX_STRIP_PANE_HEIGHT);

        //scroll pane wrapper for the comic strip
        ScrollPane scrollPane = new ScrollPane(comixStrip);
        //the value of 15 added to the default height is to account for the scroll bar
        scrollPane.setMinHeight(COMIX_STRIP_PANE_HEIGHT + 15);
        scrollPane.setFitToWidth(true);
        scrollPane.setFitToHeight(true);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.ALWAYS);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setPannable(true);
        scrollPane.setStyle("-fx-background-color: " + APP_THEME_COLOR + ";" +
                "-fx-border-color: rgba(240, 240, 240, 0.2); -fx-border-width: 1 0 0 0");

        //add the scroll pane, containing the comic strip pane, and the bar/label showing
        //the id of a panel into a vbox
        VBox bottomPaneWrapper = new VBox();
        bottomPaneWrapper.setAlignment(Pos.CENTER);
        bottomPaneWrapper.setStyle("-fx-border-color: " + BORDER_COLOR + "; -fx-border-width: 1 0 0 0");
        panelPosition = new Label("Panel - / -");
        panelPosition.setPrefSize(SCENE_WIDTH, 30);
        panelPosition.setAlignment(Pos.CENTER);
        panelPosition.setStyle("-fx-text-fill: white;");

        bottomPaneWrapper.getChildren().addAll(panelPosition,scrollPane);
        layout.setBottom(bottomPaneWrapper);
    }

    public void createTopMenuBar()
    {
        //Creating the Top Menu bar (File, View, Panel)
        fileMenu = new Menu("File");
        panelMenu = new Menu("Panel");
        helpMenu = new Menu("Help");
        messageMenu = new Menu("Messages");

        fileMenu.getItems().add(fileMenuLoadXML);
        fileMenu.getItems().add(fileMenuSaveXML);
        fileMenu.getItems().add(fileMenuCharactersDir);
        fileMenu.getItems().add(saveAsHtml);

        panelMenu.getItems().add(panelMenuNew);
        panelMenu.getItems().add(panelMenuSave);
        panelMenu.getItems().add(panelMenuDelete);

        helpMenu.getItems().add(help);
        helpMenu.getItems().add(gettingStarted);
        helpMenu.getItems().add(about);

        MenuBar topMenuBar = new MenuBar();
        topMenuBar.getMenus().addAll(fileMenu, panelMenu, helpMenu, messageMenu);

        layout.setTop(topMenuBar);
    }

    //LEFT SIDE BUTTONS
    private void createButtons()    {
        //VBox within a BorderPane within another BorderPane
        VBox leftBarButtonsWrapper = new VBox();
        leftBarButtonsWrapper.setMinWidth(LEFT_BUTTONS_PANEL_WIDTH);
        leftBarButtonsWrapper.setPrefWidth(LEFT_BUTTONS_PANEL_WIDTH);
        leftBarButtonsWrapper.setSpacing(2);
        leftBarButtonsWrapper.setAlignment(Pos.TOP_CENTER);
        leftBarButtonsWrapper.setStyle("-fx-background-color: " + APP_THEME_COLOR + ";" +
                "-fx-border-color: " + BORDER_COLOR + "; -fx-border-width: 0 1 0 0");

        ScrollPane scrollPane = new ScrollPane(leftBarButtonsWrapper);
        scrollPane.setFitToHeight(true);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setPannable(true);
        scrollPane.setStyle("-fx-background-color: " + APP_THEME_COLOR);

        importLeftCharButton = new Button("Import Left", setButtonImg( "importLeftChar.png"));
        buttonCommonStyles(importLeftCharButton);

        importRightCharButton = new Button("Import Right", setButtonImg( "importRightChar.png"));
        buttonCommonStyles(importRightCharButton);

        flipButton = new Button("Orientation", setButtonImg("flip.png"));
        buttonCommonStyles(flipButton);

        colorPalette = new ColorPicker();
        colorPalette.setMinHeight(30);
        colorPalette.setMinWidth(LEFT_BUTTONS_PANEL_WIDTH);
        colorPalette.setStyle("-fx-background-color: rgba(30, 194, 227, 0.5); -fx-background-radius: 1;" +
                "-fx-highlight-fill: white;-fx-cursor: hand");
        colorPalette.setOnAction(event ->{
            selectedColor = colorPalette.getValue();
        });

        genderSwapButton = new Button("Gender Swap", setButtonImg( "changeGender.png"));
        buttonCommonStyles(genderSwapButton);

        changeSkinToneButton = new Button("Skin Tone", setButtonImg( "bodyColor.png"));
        buttonCommonStyles(changeSkinToneButton);

        changeHairColorButton = new Button("Hair Color", setButtonImg( "hairColor.png"));
        buttonCommonStyles(changeHairColorButton);

        changeLipsColorButton = new Button("Lip Color", setButtonImg( "lipsColor.png"));
        buttonCommonStyles(changeLipsColorButton);

        addSpeechBubbleButton = new Button("Speech Bubble", setButtonImg( "speechBubbleButton.png"));
        buttonCommonStyles(addSpeechBubbleButton);

        addThoughtBubbleButton = new Button("Thought Bubble", setButtonImg( "thoughtBubbleButton.png"));
        buttonCommonStyles(addThoughtBubbleButton);

        removeBubbleButton = new Button("Remove Bubble", setButtonImg( "removeBubbleButton.png"));
        buttonCommonStyles(removeBubbleButton);

        addTextTopButton = new Button("Top Narration", setButtonImg( "narrativeTextTop.png"));
        buttonCommonStyles(addTextTopButton);
        addTextTopButton.setContextMenu(TOP_NARRATIVE_TEXT_MENU);
        setTopTextMenu();

        addTextBottomButton = new Button("Bottom Narration", setButtonImg("narrativeTextBottom.png"));
        buttonCommonStyles(addTextBottomButton);
        addTextBottomButton.setContextMenu(BOTTOM_NARRATIVE_TEXT_MENU);
        setBottomTextMenu();

        setComicTitle = new Button("Comic Title", setButtonImg("comicTitleButton.png"));
        buttonCommonStyles(setComicTitle);

        setComicCredits = new Button("Comic Credits");
        buttonCommonStyles(setComicCredits);

        leftBarButtonsWrapper.getChildren().addAll(colorPalette, importLeftCharButton, importRightCharButton, flipButton, genderSwapButton, changeSkinToneButton, changeHairColorButton,
                changeLipsColorButton, addSpeechBubbleButton, addThoughtBubbleButton, removeBubbleButton, addTextTopButton, addTextBottomButton, setComicTitle, setComicCredits);

        layout.setLeft(scrollPane);
    }

    public void createRightPaneHelp() {
        helpPageClass = new HelpPage();
        layout.setRight(helpPageClass.helpPage("HELP"));
    }

    public void createRightPaneGS() {
        helpPageClass = new HelpPage();
        layout.setRight(helpPageClass.helpPage("STARTED"));
    }

    public void createRightPaneAbout() {
        helpPageClass = new HelpPage();
        layout.setRight(helpPageClass.helpPage("ABOUT"));
    }

    private void createContextMenu(){
        SELECTED_PANEL_MENU.getItems().addAll(SAVE_PANEL, DELETE_PANEL, CHANGE_PANEL_POSITION);
        TOP_NARRATIVE_TEXT_MENU.getItems().addAll(SINGLE_LINE_OPTION_TOP, MULTI_LINES_OPTION_TOP);
        BOTTOM_NARRATIVE_TEXT_MENU.getItems().addAll(SINGLE_LINE_OPTION_BOTTOM, MULTI_LINES_OPTION_BOTTOM);
    }

    private void buttonCommonStyles(Button btn){
        btn.setStyle("-fx-background-color: rgba(0, 0, 0, 0.3); -fx-font-size: 16px;-fx-cursor: hand; -fx-background-radius: 1;"+
                "-fx-text-fill: rgb(184, 205, 217); -fx-font-weight: bold; -fx-padding: 10");
        btn.setAlignment(Pos.BASELINE_LEFT);
        btn.setGraphicTextGap(15);
        btn.prefWidthProperty().setValue(LEFT_BUTTONS_PANEL_WIDTH);
        btn.setOnMouseEntered(mouseEvent ->{
            btn.setStyle("-fx-background-color: rgba(0, 0, 0, 0.5); -fx-font-size: 16px;-fx-cursor: hand; -fx-background-radius: 1 50 50 1;"+
                    "-fx-text-fill: rgb(237, 237, 237); -fx-font-weight: bold; -fx-padding: 10");
        });
        btn.setOnMouseExited(mouseEvent -> {
            btn.setStyle("-fx-background-color: rgba(0, 0, 0, 0.3); -fx-font-size: 16px;-fx-cursor: hand; -fx-background-radius: 1;"+
                    "-fx-text-fill: rgb(184, 205, 217); -fx-font-weight: bold; -fx-padding: 10");
        });
    }

    public void setCharactersDirectory(){
        DirectoryChooser dirChooser = new DirectoryChooser();
        dirChooser.setInitialDirectory(defaultCharactersDirectory);
        File dir = dirChooser.showDialog(stage);

        if(dir != null){
            defaultCharactersDirectory = dir;
        }
    }

    public File importModel()    //Uploads character to workspace pane
    {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setInitialDirectory(defaultCharactersDirectory);
        fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("Image Files", "*.png"));
        File file = fileChooser.showOpenDialog(stage);

        return file;
    }

    private ImageView setButtonImg(String filename){
        int size = 25;
        ImageView imgV = new ImageView("/resources/"+filename);
        imgV.setFitHeight(size);
        imgV.setFitWidth(size);
        return imgV;
    }

    // sets the given character view and character to currently selected
    public void selectFrame(Selected select){
        if(selectedCharacterView != null){
            selectedCharacterView.setEffect(null);
        }
        if(select == Selected.LEFT){
            selectedCharacterView = leftCharView;
        }
        else{
            selectedCharacterView = rightCharView;
        }
        selectedCharacterView.setEffect(new DropShadow(10, Color.BLACK));
    }

    public void setSelectingHandler(EventHandler<MouseEvent> leftViewEvent, EventHandler<MouseEvent> rightViewEvent){
        leftCharView.addEventHandler(MouseEvent.MOUSE_CLICKED, leftViewEvent);
        rightCharView.addEventHandler(MouseEvent.MOUSE_CLICKED, rightViewEvent);
    }

    //deals with the selection of panels in the strip
    public void selectPanel(PanelView panel){
        if(selectedPanel != null){
            selectedPanel.setEffect(null);
        }
        selectedPanel = panel;
        selectedPanel.setEffect(new DropShadow(15, Color.TURQUOISE));
        refreshPanelPositionLabel();
    }

    //====> BUBBLE IMPORT METHODS
    private String importBubble(Image bubbleImage){
        ImageView bubble = new ImageView(bubbleImage);
        bubble.setFitWidth(30);
        bubble.setFitHeight(30);
        TextInputDialog textInput = new TextInputDialog();
        textInput.setTitle("Bubble Text");
        textInput.setGraphic(bubble);
        textInput.setHeaderText("Enter bubble text. The limit is 250 characters.");
        textInput.showAndWait();

        if(textInput.getResult() != null){
            String text = textInput.getResult();

            //if the entered string is longer than 60 characters, get only the first 60 chars
            text = (text.length() < 250 ? text : text.substring(0 , 250));
            if(selectedCharacterView == leftCharView){
                leftBubble.setImage(bubble.getImage());
                leftBubbleText.setText(text);
            }
            else if(selectedCharacterView == rightCharView){
                rightBubble.setImage(bubble.getImage());
                rightBubbleText.setText(text);
            }
            return text;
        }
        return null;
    }
    public String importSpeechBubble(){
        return importBubble(SPEECH_BUBBLE_IMAGE);
    }

    public String importThoughtBubble(){
        return importBubble(THOUGHT_BUBBLE_IMAGE);
    }

    private void bubbleTextStyle(Text text){
        text.setTextAlignment(TextAlignment.CENTER);
        text.setWrappingWidth(250);
        text.setFont(Font.font("Arial", 18));
    }
    //===> END BUBBLE IMPORT METHODS

    //===> NARRATIVE TEXT METHODS
    private String addNarrativeText() {
        TextInputDialog textInput = new TextInputDialog();
        textInput.setTitle("Narrative Text");
        textInput.setHeaderText("Enter narrative text. The limit is 250 characters.");
        textInput.showAndWait();

        if(textInput.getResult() != null){
            String text = textInput.getResult();
            //limit the narrative text to 70 characters
            text = (text.length() <= 300 ? text : text.substring(0 , 300));
            System.out.println(text.length());
            return text;
        }
        return null;
    }

    public String addNarrativeTextTop(){
        String text = addNarrativeText();

        if(text != null){
            topNarrativeText.setText(text);
            narrativeTextStyle(topNarrativeText);
            narrativeTextFormat(topNarrativeText);
            return text;
        }
        return null;
    }

    public String addNarrativeTextBottom(){
        String text = addNarrativeText();

        if(text != null){
            bottomNarrativeText.setText(text);
            narrativeTextStyle(bottomNarrativeText);
            narrativeTextFormat(bottomNarrativeText);
            return text;
        }
        return null;
    }

    private void narrativeTextStyle(Text narrativeText){
        narrativeText.setTextAlignment(TextAlignment.CENTER);
        narrativeText.setFont(Font.font("Arial"));
    }

    private void setTopTextMenu(){
        MULTI_LINES_OPTION_TOP.setOnAction(event -> narrativeTextFormatTextWrapping(topNarrativeText));
        SINGLE_LINE_OPTION_TOP.setOnAction(event -> narrativeTextFormat(topNarrativeText));
    }

    private void setBottomTextMenu(){
        MULTI_LINES_OPTION_BOTTOM.setOnAction(event -> narrativeTextFormatTextWrapping(bottomNarrativeText));
        SINGLE_LINE_OPTION_BOTTOM.setOnAction(event -> narrativeTextFormat(bottomNarrativeText));
    }

    private void narrativeTextFormat(Text narrativeText){
        int fontSize = (int)WORKING_PANE_WIDTH / 30;
        narrativeText.setFont(Font.font(fontSize));
        narrativeText.setWrappingWidth(0.0);
        while(narrativeText.getLayoutBounds().getHeight() > 40 || narrativeText.getLayoutBounds().getWidth() > WORKING_PANE_WIDTH){
            System.out.println("THE HEIGHT IS: " + narrativeText.getLayoutBounds().getHeight() + "\t FONT SIZE: " + fontSize);
            fontSize -= 1;
            narrativeText.setFont(Font.font(fontSize));
        }
        System.out.println("BNarrative width: " + narrativeText.getLayoutBounds().getWidth());
        System.out.println("BNarrative height: " + narrativeText.getLayoutBounds().getHeight());
    }

    private void narrativeTextFormatTextWrapping(Text narrativeText){
        narrativeText.setWrappingWidth(WORKING_PANE_WIDTH);
        int fontSize = (int)WORKING_PANE_WIDTH / 30;
        narrativeText.setFont(Font.font(fontSize));
        while(narrativeText.getLayoutBounds().getHeight() > 40){
            System.out.println("THE HEIGHT IS: " + narrativeText.getLayoutBounds().getHeight() + "\t FONT SIZE: " + fontSize);
            fontSize -= 1;
            narrativeText.setFont(Font.font(fontSize));
        }
        System.out.println("BNarrative width: " + narrativeText.getLayoutBounds().getWidth());
        System.out.println("BNarrative height: " + narrativeText.getLayoutBounds().getHeight());
    }
    //END NARRATIVE TEXT METHODS

    public String setComicTitleDialog(){
        TextInputDialog textInput = new TextInputDialog();
        textInput.setTitle("Comic Title");
        textInput.setHeaderText("Enter comic title. Max 100 characters.");
        textInput.showAndWait();

        String title = textInput.getResult();
        if(title != null){
            if(title.length() <= 100){
                return title;
            }
            else{
                userErrorAlert("Set title error", "Failed to set comic title. Over 100 characters entered");
            }
        }
        return null;
    }

    public String setComicCreditsDialog() {
        TextInputDialog textInput = new TextInputDialog();
        textInput.setTitle("Comic Credits");
        textInput.setHeaderText("Enter comic credits...");
        textInput.showAndWait();

        String credits = textInput.getResult();
        if(credits != null){
            if(credits.length() <= 100){
                return credits;
            }
            else{
                userErrorAlert("Set credits error", "Failed to set comic credits. Over 100 characters entered");
            }
        }
        return null;
    }

    //===> BOTTOM PANE(comicStrip) METHODS
    private Image snapshotCurrentPanel(){
        WritableImage image = new WritableImage((int)WORKING_PANE_WIDTH, (int)WORKING_PANE_HEIGHT);
        if(isCharacterSelected()){
            Effect selectedEffect = selectedCharacterView.getEffect();
            selectedCharacterView.setEffect(null);
            image = mainPane.snapshot(new SnapshotParameters(), image);
            selectedCharacterView.setEffect(selectedEffect);
            System.out.println("GRID W X H: " + mainPane.getWidth() + " " + mainPane.getHeight());
            System.out.println("IMAGE W X H: " + image.getWidth() + " " + image.getHeight());
        }
        else{
             image = mainPane.snapshot(new SnapshotParameters(), image);
        }
        return image;
    }

    public PanelView createPanel(){
        Image snapshot = snapshotCurrentPanel();
        PanelView panel = new PanelView(snapshot);
        return panel;
    }

    public PanelView editSelectedPanel(){
        Image snapshot = snapshotCurrentPanel();
        if(isPanelSelected()){
            selectedPanel.setImage(snapshot);
        }
        return selectedPanel;
    }

    private void setPanelAttributes(PanelView panel){
        panel.setStyle("-fx-border-color: black ; -fx-cursor: hand");
        panel.setFitHeight(PANEL_SIZE);
        panel.setFitWidth(PANEL_SIZE);
    }

    public void loadSelectedPanel(Image leftCharacter, Image rightCharacter, BubbleType leftBubbleType, BubbleType rightBubbleType,
                                  String leftBubbleText, String rightBubbleText, String topNarrativeText, String bottomNarrativeText){
        this.leftCharView.setImage(leftCharacter);
        this.rightCharView.setImage(rightCharacter);
        this.leftBubbleText.setText(leftBubbleText);
        this.rightBubbleText.setText(rightBubbleText);
        this.topNarrativeText.setText(topNarrativeText);
        this.bottomNarrativeText.setText(bottomNarrativeText);

        if(leftBubbleType == BubbleType.SPEECH){
            this.leftBubble.setImage(SPEECH_BUBBLE_IMAGE);
        }
        else if(leftBubbleType == BubbleType.THOUGHT){
            this.leftBubble.setImage(THOUGHT_BUBBLE_IMAGE);
        }
        else{
            this.leftBubble.setImage(null);
        }

        if(rightBubbleType == BubbleType.SPEECH){
            this.rightBubble.setImage(SPEECH_BUBBLE_IMAGE);
        }
        else if(rightBubbleType == BubbleType.THOUGHT){
            this.rightBubble.setImage(THOUGHT_BUBBLE_IMAGE);
        }
        else{
            this.rightBubble.setImage(null);
        }
        //reset selected character
        if(isCharacterSelected()){
            selectedCharacterView.setEffect(null);
            selectedCharacterView = null;
        }
    }

    public boolean confirmWorkingPaneReset(){
        Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION, "Create new panel", ButtonType.YES, ButtonType.NO);
        confirmation.setTitle("New Panel");
        confirmation.setHeaderText("Creating new panel will reset the current panel. Any unsaved changes will be lost.");
        confirmation.setContentText("Are you sure you want to continue?");
        confirmation.showAndWait();

        if(confirmation.getResult() == ButtonType.YES){
            return true;
        }
        return false;
    }

    public boolean confirmDeletePanel(){
        Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION, "Create new panel", ButtonType.YES, ButtonType.NO);
        confirmation.setTitle("Remove Panel");
        confirmation.setHeaderText("Panel removal cannot be undone and the panel cannot be recovered.");
        confirmation.setContentText("Are you sure you want to continue?");
        confirmation.showAndWait();

        if(confirmation.getResult() == ButtonType.YES){
            return true;
        }
        return false;
    }

    public boolean confirmChangingPanel() {
        Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION, "Change Panel", ButtonType.YES, ButtonType.NO);
        confirmation.setTitle("Change Panel");
        confirmation.setHeaderText("Unsaved changes will be lost when changing the panel.");
        confirmation.setContentText("Do you wish to save panel before continuing ?");
        confirmation.showAndWait();

        if(confirmation.getResult() == ButtonType.YES) {
            return true;
        }

        return false;
    }

    //loads panes based on model
    public void refreshComicStrip(ArrayList<PanelView> panels){
        clearComicStrip();
        for(PanelView panel : panels){
            setPanelAttributes(panel);
            comixStrip.getChildren().add(panel);
        }
        refreshPanelPositionLabel();
    }

    public void refreshPanelPositionLabel(){
        int numberOfPanels = comixStrip.getChildren().size();
        int panelId = 0;

        if(isPanelSelected()){
            panelId = selectedPanel.getPanelId() + 1;
        }
        String text = "Panel " + panelId + " / " + numberOfPanels;
        panelPosition.setText(text);
    }

    public String changePanelIdWindow(){
        TextInputDialog textInput = new TextInputDialog();
        textInput.setTitle("Change panel position");
        textInput.setHeaderText("Enter the new position for the selected panel");
        textInput.showAndWait();

        return textInput.getResult();
    }

    public void resetWorkingPane(){
        leftBubble.setImage(null);
        rightBubble.setImage(null);
        leftCharView.setImage(null);
        rightCharView.setImage(null);
        leftBubbleText.setText(null);
        rightBubbleText.setText(null);
        topNarrativeText.setText(null);
        bottomNarrativeText.setText(null);
        resetSelectedCharacter();
        resetSelectedPanel();
        refreshPanelPositionLabel();
    }

    public void clearComicStrip(){
        comixStrip.getChildren().removeAll(comixStrip.getChildren());
    }

    public ImageView getLeftBubble() {
        return leftBubble;
    }

    public ImageView getRightBubble() {
        return rightBubble;
    }

    public ImageView getLeftCharView() {
        return leftCharView;
    }

    public ImageView getRightCharView() {
        return rightCharView;
    }

    public ImageView getSelectedCharacterView() {
        return selectedCharacterView;
    }

    public Text getLeftBubbleText() {
        return leftBubbleText;
    }

    public Text getRightBubbleText() {
        return rightBubbleText;
    }

    public Color getSelectedColor() {
        return selectedColor;
    }

    public Button getImportLeftCharButton() {
        return importLeftCharButton;
    }

    public Button getImportRightCharButton() {
        return importRightCharButton;
    }

    public Button getFlipButton() {
        return flipButton;
    }

    public Button getGenderSwapButton() {
        return genderSwapButton;
    }

    public Button getChangeSkinToneButton() {
        return changeSkinToneButton;
    }

    public Button getChangeHairColorButton() {
        return changeHairColorButton;
    }

    public Button getChangeLipsColorButton() {
        return changeLipsColorButton;
    }

    public Button getAddSpeechBubbleButton() {
        return addSpeechBubbleButton;
    }

    public Button getAddThoughtBubbleButton() {
        return addThoughtBubbleButton;
    }

    public Button getRemoveBubbleButton() {
        return removeBubbleButton;
    }

    public Button getAddTextTopButton() {
        return addTextTopButton;
    }

    public Button getAddTextBottomButton() {
        return addTextBottomButton;
    }

    public Button getSetComicTitle() {
        return setComicTitle;
    }

    public Button getSetComicCredits() {
        return setComicCredits;
    }

    public MenuItem getFileMenuLoadXML() {
        return fileMenuLoadXML;
    }

    public MenuItem getFileMenuSaveXML() {
        return fileMenuSaveXML;
    }

    public MenuItem getSaveAsHtml() {
        return saveAsHtml;
    }

    public MenuItem getFileMenuCharactersDir() {
        return fileMenuCharactersDir;
    }

    public ContextMenu getSelectedPanelMenu(){
        return SELECTED_PANEL_MENU;
    }

    public MenuItem getPanelMenuSave() {
        return panelMenuSave;
    }

    public MenuItem getPanelMenuDelete() {
        return panelMenuDelete;
    }

    public MenuItem getPanelMenuNew() {
        return panelMenuNew;
    }

    public MenuItem getHelpPage() {
        return help;
    }

    public MenuItem getHelpStartedPage() {
        return gettingStarted;
    }

    public MenuItem getAboutPage() {
        return about;
    }

    public File getDefaultCharactersDirectory() {
        return defaultCharactersDirectory;
    }

    public File getDefaultHTMLDirectory() {
        return defaultHTMLDirectory;
    }

    public boolean isCharacterSelected(){
        return selectedCharacterView != null;
    }

    public PanelView getSelectedPanel() {
        return selectedPanel;
    }

    public boolean isPanelSelected(){
        return selectedPanel != null;
    }

    public MenuItem getSavePanel() {
        return SAVE_PANEL;
    }

    public MenuItem getDeletePanel() {
        return DELETE_PANEL;
    }

    public MenuItem getChangePanelPosition() {
        return CHANGE_PANEL_POSITION;
    }

    private void resetSelectedCharacter(){
        if(isCharacterSelected()){
            selectedCharacterView.setEffect(null);
            selectedCharacterView = null;
        }
    }

    private void resetSelectedPanel(){
        if(isPanelSelected()){
            selectedPanel.setEffect(null);
            selectedPanel = null;
        }
    }

    public File saveXMLFileWindow(){
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("XML files (*.xml)", "*.xml"));
        File file = fileChooser.showSaveDialog(stage);

        return file;
    }

    public File saveHTMLFileWindow()
    {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("HTML Files (*.html)","*.html"));
        File file = fileChooser.showSaveDialog(stage);

        return file;
    }

    public File setHTMLDirectory()
    {
        DirectoryChooser dirChooser = new DirectoryChooser();
        dirChooser.setInitialDirectory(defaultHTMLDirectory);
        File dir = dirChooser.showDialog(stage);

        if(dir != null){
            defaultHTMLDirectory = dir;
        }

        return getDefaultHTMLDirectory();
    }

    //opens up window to let user select the xml file to load
    public File loadXMLFileWindow() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setInitialDirectory(new File("./"));
        fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("XML files (*.xml)", "*.xml"));
        File file = fileChooser.showOpenDialog(stage);
        return file;
    }

    //loading confirmation alert
    public boolean confirmLoadXML(){
        Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION, "Loading new comic", ButtonType.YES, ButtonType.NO);
        confirmation.setTitle("Loading new comic strip...");
        confirmation.setHeaderText("Current project will be lost if not saved.");
        confirmation.setContentText("Are do you want to continue without saving?");
        confirmation.showAndWait();

        if(confirmation.getResult() == ButtonType.YES){
            return true;
        }
        return false;
    }

    public void userInformationAlert(String title, String msg){
        Alert information = new Alert(Alert.AlertType.INFORMATION);
        information.setTitle(title);
        information.setHeaderText(msg);
        information.showAndWait();
    }

    public void userErrorAlert(String title, String msg){
        Alert information = new Alert(Alert.AlertType.ERROR);
        information.setTitle(title);
        information.setHeaderText(msg);
        information.showAndWait();
    }
}

