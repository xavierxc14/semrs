package com.googlecode.semrs.client;


import java.util.Date;

import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.i18n.client.NumberFormat;
import com.google.gwt.user.client.History;
import com.googlecode.semrs.client.screens.admin.AddEditUserScreen;
import com.googlecode.semrs.client.screens.admin.AdminGroupsScreen;
import com.googlecode.semrs.client.screens.admin.AdminRolesScreen;
import com.googlecode.semrs.client.screens.admin.AdminUsersScreen;
import com.googlecode.semrs.client.screens.dictionary.AdminComplicationsScreen;
import com.googlecode.semrs.client.screens.dictionary.AdminDiseaseScreen;
import com.googlecode.semrs.client.screens.dictionary.AdminDrugsScreen;
import com.googlecode.semrs.client.screens.dictionary.AdminLabTestsScreen;
import com.googlecode.semrs.client.screens.dictionary.AdminProceduresScreen;
import com.googlecode.semrs.client.screens.dictionary.AdminSymptomsScreen;
import com.googlecode.semrs.client.screens.dictionary.AdminTreatmentsScreen;
import com.googlecode.semrs.client.screens.patient.AddPatientScreen;
import com.googlecode.semrs.client.screens.patient.AdminEncounterScreen;
import com.googlecode.semrs.client.screens.patient.AdminPatientScreen;
import com.googlecode.semrs.client.screens.patient.ListEncountersScreen;
import com.googlecode.semrs.client.screens.patient.ListPatientsScreen;
import com.gwtext.client.core.Connection;
import com.gwtext.client.core.EventObject;
import com.gwtext.client.core.Function;
import com.gwtext.client.core.Position;
import com.gwtext.client.core.RegionPosition;
import com.gwtext.client.core.UrlParam;
import com.gwtext.client.data.DateFieldDef;
import com.gwtext.client.data.FieldDef;
import com.gwtext.client.data.HttpProxy;
import com.gwtext.client.data.JsonReader;
import com.gwtext.client.data.Record;
import com.gwtext.client.data.RecordDef;
import com.gwtext.client.data.SimpleStore;
import com.gwtext.client.data.Store;
import com.gwtext.client.data.StringFieldDef;
import com.gwtext.client.data.event.StoreListenerAdapter;
import com.gwtext.client.util.Format;
import com.gwtext.client.widgets.Button;
import com.gwtext.client.widgets.MessageBox;
import com.gwtext.client.widgets.MessageBoxConfig;
import com.gwtext.client.widgets.PagingToolbar;
import com.gwtext.client.widgets.Panel;
import com.gwtext.client.widgets.Resizable;
import com.gwtext.client.widgets.ResizableConfig;
import com.gwtext.client.widgets.TabPanel;
import com.gwtext.client.widgets.Tool;
import com.gwtext.client.widgets.Toolbar;
import com.gwtext.client.widgets.ToolbarButton;
import com.gwtext.client.widgets.Window;
import com.gwtext.client.widgets.chart.yui.PieChart;
import com.gwtext.client.widgets.event.ButtonListenerAdapter;
import com.gwtext.client.widgets.event.PanelListenerAdapter;
import com.gwtext.client.widgets.event.ResizableListenerAdapter;
import com.gwtext.client.widgets.form.ComboBox;
import com.gwtext.client.widgets.form.DateField;
import com.gwtext.client.widgets.form.Field;
import com.gwtext.client.widgets.form.FieldSet;
import com.gwtext.client.widgets.form.Form;
import com.gwtext.client.widgets.form.FormPanel;
import com.gwtext.client.widgets.form.HtmlEditor;
import com.gwtext.client.widgets.form.TextArea;
import com.gwtext.client.widgets.form.TextField;
import com.gwtext.client.widgets.form.event.FieldListenerAdapter;
import com.gwtext.client.widgets.form.event.FormListenerAdapter;
import com.gwtext.client.widgets.form.event.FormPanelListenerAdapter;
import com.gwtext.client.widgets.grid.CellMetadata;
import com.gwtext.client.widgets.grid.ColumnConfig;
import com.gwtext.client.widgets.grid.ColumnModel;
import com.gwtext.client.widgets.grid.GridPanel;
import com.gwtext.client.widgets.grid.GridView;
import com.gwtext.client.widgets.grid.Renderer;
import com.gwtext.client.widgets.grid.RowParams;
import com.gwtext.client.widgets.grid.RowSelectionModel;
import com.gwtext.client.widgets.grid.event.GridCellListener;
import com.gwtext.client.widgets.layout.AnchorLayoutData;
import com.gwtext.client.widgets.layout.BorderLayout;
import com.gwtext.client.widgets.layout.BorderLayoutData;
import com.gwtext.client.widgets.layout.ColumnLayout;
import com.gwtext.client.widgets.layout.ColumnLayoutData;
import com.gwtext.client.widgets.layout.FitLayout;
import com.gwtext.client.widgets.layout.FormLayout;
import com.gwtext.client.widgets.layout.VerticalLayout;



public abstract class ShowcasePanel extends Panel {

    private Toolbar toolbar;

    protected static final String EVENT = "event";
    protected static final String MESSAGE = "message";
    
    public static final AdminUsersScreen adminUsersScreen = new AdminUsersScreen();
    
    public static final AddEditUserScreen addEditUserScreen = new AddEditUserScreen();
	
    public static final AdminRolesScreen adminRolesScreen = new AdminRolesScreen();

   // public static final AdminPermScreen adminPermScreen = new AdminPermScreen();
    
    public static final AdminGroupsScreen adminGroupsScreen = new AdminGroupsScreen();
    
    public static final ListPatientsScreen listPatientsScreen = new ListPatientsScreen();
    
    public static final AddPatientScreen addPatientScreen = new AddPatientScreen();
    
    public static final AdminEncounterScreen adminEncounterScreen = new AdminEncounterScreen();
    
    public static final AdminSymptomsScreen adminSymptomsScreen = new AdminSymptomsScreen();
    
    public static final AdminDiseaseScreen adminDiseaseScreen = new AdminDiseaseScreen();
    
    public static final AdminLabTestsScreen adminLabTestsScreen = new AdminLabTestsScreen();
    
    public static final AdminTreatmentsScreen adminTreatmentsScreen = new AdminTreatmentsScreen();
    
    public static final AdminDrugsScreen adminDrugsScreen = new AdminDrugsScreen();
    
    public static final AdminProceduresScreen adminProceduresScreen = new AdminProceduresScreen();
    
    public static final AdminComplicationsScreen adminComplicationsScreen = new AdminComplicationsScreen();

    public static final AdminPatientScreen adminPatientScreen = new AdminPatientScreen();
    
    public static final ListEncountersScreen listEncountersScreen = new ListEncountersScreen();
  
    public Record gridWindowRecord;
    
    public Record deleteRecord;
    
    private String severity;
    
    private RecordDef recordDef;
    private Store store;

    protected Panel panel;
    

    
    

    protected ShowcasePanel() {
        setTitle(getTitle());
        setClosable(true);
        setTopToolbar(new Toolbar());
        setPaddings(20);
        setLayout(new FitLayout());
        setBorder(false);
        setAutoScroll(true);
        addListener(new PanelListenerAdapter() {
            public void onActivate(Panel panel) {
                ShowcasePanel.this.onActivate();
            }
        });
    }

    protected void onActivate() {
        Panel viewPanel = getViewPanel();
        if (viewPanel instanceof Window) {
            ((Window) viewPanel).show();
        }
    }

    protected void afterRender() {

    	/*
        ButtonListenerAdapter listener = new ButtonListenerAdapter() {
            public void onClick(Button button, EventObject e) {
                Window window = getSourceWindow();
                window.show(sourceButton.getId());
            }
        };

        sourceButton = new ToolbarButton("View Source", listener);
        sourceButton.setIconCls(sourceIconCls);

        if (getSourceUrl() == null) {
            sourceButton.setDisabled(true);
        }
        */
        toolbar = getTopToolbar();

        toolbar.addFill();
        //toolbar.addButton(sourceButton);
      
        addViewPanel();
    }

    private void addViewPanel() {
        Panel mainPanel = new Panel();
        mainPanel.setBorder(false);
        //mainPanel.setLayout(new BorderLayout());
        mainPanel.setLayout(new BorderLayout());

        Panel viewPanel = getViewPanel();
        if (viewPanel instanceof Window) {
            viewPanel.show();
            viewPanel = new Panel();
        }
        viewPanel.setAutoScroll(true);
        viewPanel.setBorder(false);

        BorderLayoutData centerLayoutData = new BorderLayoutData(RegionPosition.CENTER);
        //centerLayoutData.setMargins(new Margins(15, 15, 15, 15));
        //com.google.gwt.user.client.Window.alert("viewpanel dolayout");
        viewPanel.doLayout();
        mainPanel.add(viewPanel, centerLayoutData);


        boolean hasIntro = getIntro() != null;

        if (hasIntro) {

            Panel rightPanel = new Panel();
            rightPanel.setPaddings(0, 15, 0, 0);
            rightPanel.setWidth(210);
            rightPanel.setLayout(new VerticalLayout(15));

            Panel introPanel = new Panel();
            introPanel.setIconCls("book-icon");
            introPanel.setFrame(true);
            introPanel.setTitle("Overview");
            introPanel.setHtml("<p>" + getIntro() + "</p>");

            //introPanel.setPaddings(0, 0, 0, 15);
            rightPanel.add(introPanel);
            if (showEvents()) {
                GridPanel loggerGrid = getLoggerGrid();
                loggerGrid.setWidth(195);
                loggerGrid.setHeight(300);
                rightPanel.add(loggerGrid);
            }


            BorderLayoutData eastLayoutData = new BorderLayoutData(RegionPosition.EAST);
            //eastLayoutData.setMargins(new Margins(0, 15, 0, 0));
            mainPanel.add(rightPanel, eastLayoutData);
        }
       // com.google.gwt.user.client.Window.alert("mainPanel dolayout");
        mainPanel.doLayout();
        add(mainPanel);
    }

    protected void log(String eventType, String messsage) {
        if (recordDef != null) {
            Record record = recordDef.createRecord(new Object[]{
                    new Date(),
                    eventType,
                    messsage
            });
            store.insert(0, record);
        }
    }

    protected boolean showEvents() {
        return false;
    }



    private GridPanel getLoggerGrid() {

        recordDef = new RecordDef(new FieldDef[]{
                new DateFieldDef("date"),
                new StringFieldDef("type"),
                new StringFieldDef("message")
        });

        store = new Store(recordDef);

        ColumnModel columnModel = new ColumnModel(new ColumnConfig[]{
                new ColumnConfig("&nbsp", "type", 3, true, new Renderer() {
                    public String render(Object value, CellMetadata cellMetadata, Record record, int rowIndex, int colNum, Store store) {
                        if (value.equals(EVENT)) {
                            String someTip = "Event";
                            cellMetadata.setHtmlAttribute("style=\"background:#FFFF88;padding:5px\"");
                        } else {
                            cellMetadata.setHtmlAttribute("style=\"background:#4096EE;padding:5px\"");
                        }
                        return "";
                    }
                }),

                new ColumnConfig("Time", "date", 70, true, new Renderer() {
                    public String render(Object value, CellMetadata cellMetadata, Record record, int rowIndex, int colNum, Store store) {
                        DateTimeFormat formatter = DateTimeFormat.getFormat("hh:mm:ss");
                        return formatter.format((Date) value);
                    }
                })
        });

        GridPanel gridPanel = new GridPanel(store, columnModel);
        gridPanel.setTitle("Logger");
        gridPanel.setAutoExpandColumn("date");
        gridPanel.addTool(new Tool(Tool.MINUS, new Function() {
            public void execute() {
                store.removeAll();
                store.commitChanges();
            }
        }, "Clear Log"));

        GridView view = new GridView() {
            public String getRowClass(Record record, int index, RowParams rowParams, Store store) {
                rowParams.setBody(Format.format("<p>{0}</p>", record.getAsString("message")));
                return "";
            }
        };

        view.setEnableRowBody(true);
        view.setForceFit(true);
        gridPanel.setView(view);

        return gridPanel;
    }

    public String getSourceUrl() {
        return null;
    }

    public String getHtmlUrl() {
        return null;
    }

    public String getXmlDataUrl() {
        return null;
    }

    public String getJsonDataUrl() {
        return null;
    }

    public String getCssUrl() {
        return null;
    }

    public String getIntro() {
        return null;
    }

    public abstract Panel getViewPanel();
    
    
    public void showScreen(TabPanel appTabPanel, Panel panel, String title, String iconCls, String screenName) {
        String panelID = panel.getId();
        if (appTabPanel.hasItem(panelID)) {
        	//panel.doLayout();
            appTabPanel.scrollToTab(panel, true);
            appTabPanel.activate(panelID);
            //appTabPanel.doLayout();
        } else {
            if (!panel.isRendered()) {
                panel.setTitle(title);
                if (iconCls == null) {
                    iconCls = "plugins-nav-icon";
                }
                panel.setIconCls(iconCls);
            }
            appTabPanel.add(panel);
           // appTabPanel.doLayout();
            appTabPanel.activate(panel.getId());
           // appTabPanel.doLayout();
            //panel.show();
        }
        //panel.show();
        appTabPanel.doLayout();
        History.newItem(screenName);
    }

    public String getRecordValues(Record[] records){

		String recordValues = "";

		for(int i = 0 ; i<records.length; i++){

			if(i<records.length-1){
				recordValues += records[i].getAsString("id") + ",";
			}else{
				recordValues += records[i].getAsString("id");
			}
		}

		return recordValues;

	}
    
    public Window getGridWindow(final Store from, final Store to, String title, String iconClass){

    	final Window gridWindow =  new com.gwtext.client.widgets.Window();  

    	gridWindow.setTitle(title);  
    	gridWindow.setWidth(750);  
    	gridWindow.setHeight(550);    
    	gridWindow.setLayout(new FitLayout());  
    	gridWindow.setPaddings(5);  
    	gridWindow.setResizable(false);
    	gridWindow.setButtonAlign(Position.CENTER);  
    	gridWindow.setModal(true);
    	gridWindow.setIconCls(iconClass);
    	gridWindow.setCloseAction(com.gwtext.client.widgets.Window.HIDE);  
    	ResizableConfig config = new ResizableConfig();  
	    config.setHandles(Resizable.ALL);  
    	final Resizable resizable = new Resizable(gridWindow, config);  
        resizable.addListener(new ResizableListenerAdapter() {  
              public void onResize(Resizable self, int width, int height) {  
            	  gridWindow.setWidth(width);  
            	  gridWindow.setHeight(height);
           	   
              }  
         });  
    	

    	final FormPanel gridForm = new FormPanel(); 
    	gridForm.setFrame(true);  
    	gridForm.setWidth(750);  
    	gridForm.setHeight(550);
    	gridForm.setAutoScroll(true);
    	gridForm.setPaddings(5, 5, 5, 0);  
    	gridForm.setLabelAlign(Position.TOP); 
    	
    	
    	Panel topPanel = new Panel();  
    	topPanel.setLayout(new ColumnLayout());  

    	//create first panel and add fields to it  
    	Panel columnOnePanel = new Panel();  
    	columnOnePanel.setLayout(new FormLayout());  

    	TextField id = new TextField("C&oacute;digo", "id");
    	columnOnePanel.add(id, new AnchorLayoutData("65%"));  

    	topPanel.add(columnOnePanel, new ColumnLayoutData(.5));  

    	Panel columnTwoPanel = new Panel();  
    	columnTwoPanel.setLayout(new FormLayout());  

    	TextField labTestName = new TextField("Nombre", "name");  
    	columnTwoPanel.add(labTestName, new AnchorLayoutData("65%")); 
    	topPanel.add(columnTwoPanel, new ColumnLayoutData(0.5));  



    	FieldSet fieldSet = new FieldSet();
    	fieldSet.add(topPanel);


    	Panel proxyPanel = new Panel();  
    	proxyPanel.setBorder(true);  
    	proxyPanel.setBodyBorder(false);
    	proxyPanel.setCollapsible(false);  
    	proxyPanel.setLayout(new FormLayout());
    	proxyPanel.setButtonAlign(Position.CENTER);

    	Button clear = new Button("Limpiar");
    	clear.setIconCls("clear-icon");
    	clear.addListener(new ButtonListenerAdapter(){

    		public void onClick(Button button, EventObject e){
    			gridForm.getForm().reset();

    		}

    	});
    	proxyPanel.addButton(clear);  

    	final PagingToolbar pagingToolbar = new PagingToolbar(from); 
    	pagingToolbar.setPageSize(20);
    	pagingToolbar.setDisplayInfo(true);
    	pagingToolbar.setEmptyMsg("No hay registros");
    	pagingToolbar.setDisplayMsg("Mostrando Registros {0} - {1} de {2}");
    	from.load(0, pagingToolbar.getPageSize());
    	MainPanel.resetTimer();
    	
    	final ToolbarButton button = new ToolbarButton("Agregar", new ButtonListenerAdapter() {  
    		public void onClick(Button button, EventObject e) { 
    			if(to.getRecords()!=null && to.getRecords().length>0){
    			for(int i =0; i<to.getRecords().length;++i){
    				if(getGridWindowRecord().getAsString("id").equals(to.getRecords()[i].getAsString("id"))){
    	    			from.remove(getGridWindowRecord());
    					button.setDisabled(true);
    					break;
    				}else if(i==to.getRecords().length-1){
    					to.add(getGridWindowRecord());			
    	    			from.remove(getGridWindowRecord());
    	    			button.setDisabled(true);		
    				}
    			}
    			
    			}else{
    				to.add(getGridWindowRecord());
	    			from.remove(getGridWindowRecord());
	    			button.setDisabled(true);
    			}

    		}  
    	});  
    	from.addStoreListener(new StoreListenerAdapter() {
    	       	public void onLoadException(Throwable error) {
    	       		
    	 	       MessageBox.alert("Error", "Ha ocurrido un error al tratar de obtener la lista de registros.");
    			}
    		
    	});
    	button.setIconCls("add-icon");
    	button.setDisabled(true);
    	pagingToolbar.addButton(button);  

    	final Button search = new Button("Buscar");  
    	search.setIconCls("search-icon");  
    	search.addListener(new ButtonListenerAdapter(){

    		public void onClick(Button button, EventObject e){
    			UrlParam[] params = getFormData(gridForm.getForm());
    			from.setBaseParams(params);
    			from.load(0, pagingToolbar.getPageSize());
    			pagingToolbar.updateInfo();
    			MainPanel.resetTimer();
    		}

    	});
    	proxyPanel.addButton(search);  
    	fieldSet.add(proxyPanel);
    	gridForm.add(fieldSet);  
    	// formPanel.add(proxyPanel);

    	gridForm.setMonitorValid(true);
    	gridForm.addListener(new FormPanelListenerAdapter() {
    		public void onClientValidation(FormPanel formPanel, boolean valid) {
    			search.setDisabled(!valid);
    		}
    	});


    	GridView view = new GridView();
    	view.setEmptyText("No hay Registros");
    	view.setAutoFill(true);
    	view.setForceFit(true);





    	GridPanel grid = new GridPanel(from,createColModel());
    	grid.setEnableDragDrop(false);
    	grid.setWidth(705);
    	grid.setHeight(350);
    	grid.setLoadMask(true);  
    	grid.setSelectionModel(new RowSelectionModel());  
    	grid.setFrame(true);  
    	grid.setView(view);
    	grid.setTitle(title);
    	grid.addGridCellListener(new GridCellListener() {  


    		public void onCellDblClick(GridPanel grid, int rowIndex,
    				int colIndex, EventObject e) {
    		}


    		public void onCellClick(GridPanel grid, int rowIndex,
    				int colIndex, EventObject e) {
    			setGridWindowRecord(grid.getStore().getAt(rowIndex));
    			button.setDisabled(false);

    		}


    		public void onCellContextMenu(GridPanel grid, int rowIndex,
    				int cellIndex, EventObject e) {
    			// TODO Auto-generated method stub

    		}
    	});  
    	grid.setBottomToolbar(pagingToolbar);
    	gridForm.add(grid);
    	gridWindow.add(gridForm);

    	return gridWindow;
    }

	public Record getGridWindowRecord() {
		return gridWindowRecord;
	}

	public void setGridWindowRecord(Record gridWindowRecord) {
		this.gridWindowRecord = gridWindowRecord;
	}


	public UrlParam[] getFormData(Form form){
		
		String formValues = form.getValues();	    	
		String[] nameValuePairs = formValues.split("&");
		UrlParam[] params = new UrlParam[nameValuePairs.length];
		for(int i= 0; i<=nameValuePairs.length; i++){
			String[] item = nameValuePairs[i].split("=");
			params[i]= new UrlParam(item[0], item[1]);
			
		}
		
		return params;
		
	}
	

	
	static ColumnModel createColModel() {

		ColumnModel colModel = null;

			colModel = new ColumnModel(new ColumnConfig[] { 

					new ColumnConfig("C&oacute;digo", "id"),
					new ColumnConfig("Nombre", "name")

			});
	
			for (int i = 0; i < colModel.getColumnConfigs().length; i++){
				((ColumnConfig) colModel.getColumnConfigs()[i]).setSortable(true);
			}
		
		return colModel;
	}
	
	public GridPanel getGrid(final Store gridStore,final Store toStore ,ColumnModel colModel, final String title, final String iconCls){

		    GridView view = new GridView();
	        view.setEmptyText("No hay Registros");
	        view.setAutoFill(true);
	        view.setForceFit(true);
	    
	        GridPanel grid = new GridPanel(gridStore, colModel);
	        grid.setEnableDragDrop(false);
	        grid.setWidth(500);
	        grid.setHeight(300);
	        grid.setLoadMask(true);  
	        grid.setSelectionModel(new RowSelectionModel());  
	        grid.setFrame(true);  
	        grid.setView(view);
	        grid.setTitle(title);
	        
	        //grid.setHideColumnHeader(true);
	        //grid.setId("labTestDiseases");
	        
	      Toolbar toolbar = new Toolbar();  
          final ToolbarButton addButton = new ToolbarButton("Agregar", new ButtonListenerAdapter() {  
             public void onClick(Button button, EventObject e) {  
     		    //gridStore.load();
          	 getGridWindow(toStore,gridStore,title,iconCls).show();
               
             }  
          });  
          addButton.setIconCls("add-icon");
          toolbar.addButton(addButton);  
          toolbar.addSeparator();
          
          final ToolbarButton deleteButton = new ToolbarButton("Eliminar", new ButtonListenerAdapter() {  
              public void onClick(Button button, EventObject e) { 
              	 gridStore.remove(getDeleteRecord());
              	 button.setDisabled(true);
              }  
           });  
          
         
          deleteButton.setIconCls("delete-icon");
          deleteButton.setDisabled(true);
          toolbar.addButton(deleteButton); 
          toolbar.addSeparator();
          
          final ToolbarButton refreshButton = new ToolbarButton("Refrescar", new ButtonListenerAdapter() {  
              public void onClick(Button button, EventObject e) { 
            	  gridStore.reload();
              }  
           });  
          refreshButton.setIconCls("refresh-icon");
          toolbar.addButton(refreshButton);
          
          grid.setBottomToolbar(toolbar);
	        
	        grid.addGridCellListener(new GridCellListener() {  

				
				public void onCellDblClick(GridPanel grid, int rowIndex,
						int colIndex, EventObject e) {
				}

				
				public void onCellClick(GridPanel grid, int rowIndex,
						int colIndex, EventObject e) {
					setDeleteRecord(grid.getStore().getAt(rowIndex));
					deleteButton.setDisabled(false);
					
				}

	
				public void onCellContextMenu(GridPanel grid, int rowIndex,
						int cellIndex, EventObject e) {
					// TODO Auto-generated method stub
					
				}
         });  
	        
	        return grid;
	}
	
	public GridPanel getSearchGrid(final Store gridStore,final Store toStore ,ColumnModel colModel, final String title, final String iconCls){

	    GridView view = new GridView();
        view.setEmptyText("No hay Registros");
        view.setAutoFill(true);
        view.setForceFit(true);
    
        
       ColumnModel searchColModel =  new ColumnModel(new ColumnConfig[] { 
      		  new ColumnConfig("C&oacute;digo", "id"),
	              new ColumnConfig("Nombre", "name")
	      		
	      });
        for (int i = 0; i < searchColModel.getColumnConfigs().length; i++){
	          ((ColumnConfig) searchColModel.getColumnConfigs()[i]).setSortable(true);
	      }
        GridPanel grid = new GridPanel(gridStore,searchColModel);
        grid.setEnableDragDrop(false);
        grid.setWidth(500);
        grid.setHeight(200);
        grid.setLoadMask(true);  
        grid.setSelectionModel(new RowSelectionModel());  
        grid.setFrame(true);  
        grid.setView(view);
        grid.setTitle(title);
        //grid.setHideColumnHeader(true);
        
        //grid.setHideColumnHeader(true);
        //grid.setId("labTestDiseases");
        
      Toolbar toolbar = new Toolbar();  
      final ToolbarButton addButton = new ToolbarButton("Agregar", new ButtonListenerAdapter() {  
         public void onClick(Button button, EventObject e) {  
 		    //gridStore.load();
      	 getGridWindow(toStore,gridStore,title,iconCls).show();
           
         }  
      });  
      addButton.setIconCls("add-icon");
      toolbar.addButton(addButton);  
      toolbar.addSeparator();
      
      final ToolbarButton deleteButton = new ToolbarButton("Eliminar", new ButtonListenerAdapter() {  
          public void onClick(Button button, EventObject e) { 
          	 gridStore.remove(getDeleteRecord());
          	 button.setDisabled(true);
          }  
       });  
      
     
      deleteButton.setIconCls("delete-icon");
      deleteButton.setDisabled(true);
      toolbar.addButton(deleteButton); 
      toolbar.addSeparator();
      
      final ToolbarButton clearButton = new ToolbarButton("Limpiar", new ButtonListenerAdapter() {  
          public void onClick(Button button, EventObject e) { 
        	  gridStore.removeAll();
          }  
       });  
      clearButton.setIconCls("clear-icon");
      toolbar.addButton(clearButton);
      
      grid.setBottomToolbar(toolbar);
        
        grid.addGridCellListener(new GridCellListener() {  

			
			public void onCellDblClick(GridPanel grid, int rowIndex,
					int colIndex, EventObject e) {
			}

			
			public void onCellClick(GridPanel grid, int rowIndex,
					int colIndex, EventObject e) {
				setDeleteRecord(grid.getStore().getAt(rowIndex));
				deleteButton.setDisabled(false);
				
			}


			public void onCellContextMenu(GridPanel grid, int rowIndex,
					int cellIndex, EventObject e) {
				// TODO Auto-generated method stub
				
			}
     });  
        
        return grid;
}
	
	
	 public ColumnModel createColModel(boolean showDates, boolean sort) {
		   
		  ColumnModel colModel = null;
		 if(showDates){ 
			 colModel = new ColumnModel(new ColumnConfig[] { 
	      		new ColumnConfig("C&oacute;digo", "id"),
	              new ColumnConfig("Nombre", "name"), 
	             // new ColumnConfig("Descripci&oacute;n", "description"), 
	              new ColumnConfig("F.Modificaci&oacute;n", "lastEditDate"),
	              new ColumnConfig("U.Modificaci&oacute;n", "lastEditUser")
	      		
	      });
		 }else{
			 colModel = new ColumnModel(new ColumnConfig[] { 
			      		new ColumnConfig("C&oacute;digo", "id"),
			            new ColumnConfig("Nombre", "name")
			      });
			 
		 }
		 if(sort){
	      for (int i = 0; i < colModel.getColumnConfigs().length; i++){
	          ((ColumnConfig) colModel.getColumnConfigs()[i]).setSortable(true);
	      }
		 }
	      return colModel;
	  }
	 
	 
	 public GridPanel getSymptomsGrid(final Store gridStore,final boolean edit, ColumnModel colModel, final String title){

		    GridView view = new GridView();
	        view.setEmptyText("No hay Registros");
	        view.setAutoFill(true);
	        view.setForceFit(true);
	    
	        GridPanel grid = new GridPanel(gridStore, colModel);
	        grid.setEnableDragDrop(false);
	        grid.setWidth(530);
	        grid.setHeight(300);
	        grid.setLoadMask(true);  
	        grid.setSelectionModel(new RowSelectionModel());  
	        grid.setFrame(true);  
	        grid.setView(view);
	        //grid.setTitle(title);

	        
			FieldDef[] gridFieldDefs = new FieldDef[] { 
  		     		new StringFieldDef("id"),
  		            new StringFieldDef("name")
  		  };

  		RecordDef gridRecordDef = new RecordDef(gridFieldDefs);
  		JsonReader gridReader = new JsonReader("response.value.items", gridRecordDef);
  		HttpProxy gridProxy = new HttpProxy("/semrs/diseaseServlet", Connection.GET);
  		final Store diseaseStore = new Store(gridProxy,gridReader,true);
  		
  		HttpProxy gridProxyS = new HttpProxy("/semrs/symptomServlet", Connection.GET);
  		final Store symptomStore = new Store(gridProxyS,gridReader,true);
	        
	      Toolbar toolbar = new Toolbar();  
       final ToolbarButton addSymptomButton = new ToolbarButton("Agregar S&iacute;ntoma", new ButtonListenerAdapter() {  
          public void onClick(Button button, EventObject e) {  
  		    //gridStore.load();
        	  getEncounterGridWindow(symptomStore,gridStore,"S&iacute;ntomas","symptoms-icon",true,false,false,false,false,false).show();
            
          }  
       });  
       addSymptomButton.setIconCls("add-icon");
       if(!edit){
    	   addSymptomButton.setDisabled(true);
       }
       toolbar.addButton(addSymptomButton);  
       toolbar.addSeparator();
       
       final ToolbarButton addDiseaseButton = new ToolbarButton("Agregar Enfermedad", new ButtonListenerAdapter() {  
           public void onClick(Button button, EventObject e) {  
   		    //gridStore.load();
        	   getEncounterGridWindow(diseaseStore,gridStore,"Enfermedades","disease-icon",false,true,false,false,false,false).show();
             
           }  
        });  
        addDiseaseButton.setIconCls("add-icon");
        if(!edit){
        	addDiseaseButton.setDisabled(true);
        }
        toolbar.addButton(addDiseaseButton);  
        toolbar.addSeparator();
       
       
       final ToolbarButton deleteButton = new ToolbarButton("Eliminar", new ButtonListenerAdapter() {  
           public void onClick(Button button, EventObject e) { 
           	 gridStore.remove(getDeleteRecord());
           	 button.setDisabled(true);
           }  
        });  
       
      
       deleteButton.setIconCls("delete-icon");
       deleteButton.setDisabled(true);
       toolbar.addButton(deleteButton); 
       toolbar.addSeparator();
       
       final ToolbarButton refreshButton = new ToolbarButton("Refrescar", new ButtonListenerAdapter() {  
           public void onClick(Button button, EventObject e) { 
         	  gridStore.removeAll();
           }  
        });  
       refreshButton.setIconCls("refresh-icon");
       if(!edit){
    		 refreshButton.setDisabled(true);
    	 }
       toolbar.addButton(refreshButton);
       
       grid.setBottomToolbar(toolbar);
	        
	        grid.addGridCellListener(new GridCellListener() {  

				
				public void onCellDblClick(GridPanel grid, int rowIndex,
						int colIndex, EventObject e) {
				}

				
				public void onCellClick(GridPanel grid, int rowIndex,
						int colIndex, EventObject e) {
					if(edit){
					setDeleteRecord(grid.getStore().getAt(rowIndex));
					deleteButton.setDisabled(false);
					}
					
				}

	
				public void onCellContextMenu(GridPanel grid, int rowIndex,
						int cellIndex, EventObject e) {
					// TODO Auto-generated method stub
					
				}
      });  
	        
	        return grid;
	}
	 
	 
	 public GridPanel getLabTestGrid(final Store gridStore,final boolean edit, ColumnModel colModel, final String title){

		    GridView view = new GridView();
	        view.setEmptyText("No hay Registros");
	        view.setAutoFill(true);
	        view.setForceFit(true);
	    
	        GridPanel grid = new GridPanel(gridStore, colModel);
	        grid.setEnableDragDrop(false);
	        grid.setWidth(530);
	        grid.setHeight(300);
	        grid.setLoadMask(true);  
	        grid.setSelectionModel(new RowSelectionModel());  
	        grid.setFrame(true);  
	        grid.setView(view);
	        //grid.setTitle(title);

	        
			FieldDef[] gridFieldDefs = new FieldDef[] { 
		     		new StringFieldDef("id"),
		            new StringFieldDef("name")
		  };

		RecordDef gridRecordDef = new RecordDef(gridFieldDefs);
		JsonReader gridReader = new JsonReader("response.value.items", gridRecordDef);
		HttpProxy gridProxy = new HttpProxy("/semrs/labTestServlet", Connection.GET);
		final Store labTestStore = new Store(gridProxy,gridReader,true);
		

	        
	      Toolbar toolbar = new Toolbar();  
    final ToolbarButton addSymptomButton = new ToolbarButton("Agregar Ex&aacute;men", new ButtonListenerAdapter() {  
       public void onClick(Button button, EventObject e) {  
		    //gridStore.load();
     	  getEncounterGridWindow(labTestStore,gridStore,"Ex&aacute;menes de Laboratorio","labtests-icon",false,false,true,false,false,false).show();
         
       }  
    });  
    addSymptomButton.setIconCls("add-icon");
    if(!edit){
 	   addSymptomButton.setDisabled(true);
    }
    toolbar.addButton(addSymptomButton);  
    toolbar.addSeparator();
      
    
    final ToolbarButton deleteButton = new ToolbarButton("Eliminar", new ButtonListenerAdapter() {  
        public void onClick(Button button, EventObject e) { 
        	 gridStore.remove(getDeleteRecord());
        	 button.setDisabled(true);
        }  
     });  
    
   
    deleteButton.setIconCls("delete-icon");
    deleteButton.setDisabled(true);
    toolbar.addButton(deleteButton); 
    toolbar.addSeparator();
    
    final ToolbarButton refreshButton = new ToolbarButton("Refrescar", new ButtonListenerAdapter() {  
        public void onClick(Button button, EventObject e) { 
      	  gridStore.removeAll();
        }  
     });  
    refreshButton.setIconCls("refresh-icon");
    if(!edit){
   	 refreshButton.setDisabled(true);
    }
    toolbar.addButton(refreshButton);
    
    grid.setBottomToolbar(toolbar);
	        
	        grid.addGridCellListener(new GridCellListener() {  

				
				public void onCellDblClick(GridPanel grid, int rowIndex,
						int colIndex, EventObject e) {
				}

				
				public void onCellClick(GridPanel grid, int rowIndex,
						int colIndex, EventObject e) {
				if(edit){
					setDeleteRecord(grid.getStore().getAt(rowIndex));
					deleteButton.setDisabled(false);
				}
				}

	
				public void onCellContextMenu(GridPanel grid, int rowIndex,
						int cellIndex, EventObject e) {
					// TODO Auto-generated method stub
					
				}
   });  
	        
	        return grid;
	}
	 
	 
	 public GridPanel getProcedureGrid(final Store gridStore,final boolean edit, ColumnModel colModel, final String title){

		 GridView view = new GridView();
		 view.setEmptyText("No hay Registros");
		 view.setAutoFill(true);
		 view.setForceFit(true);

		 GridPanel grid = new GridPanel(gridStore, colModel);
		 grid.setEnableDragDrop(false);
		 grid.setWidth(530);
		 grid.setHeight(300);
		 grid.setLoadMask(true);  
		 grid.setSelectionModel(new RowSelectionModel());  
		 grid.setFrame(true);  
		 grid.setView(view);
		 //grid.setTitle(title);


		 FieldDef[] gridFieldDefs = new FieldDef[] { 
				 new StringFieldDef("id"),
				 new StringFieldDef("name")
		 };

		 RecordDef gridRecordDef = new RecordDef(gridFieldDefs);
		 JsonReader gridReader = new JsonReader("response.value.items", gridRecordDef);
		 HttpProxy gridProxy = new HttpProxy("/semrs/procedureServlet", Connection.GET);
		 final Store labTestStore = new Store(gridProxy,gridReader,true);



		 Toolbar toolbar = new Toolbar();  
		 final ToolbarButton addSymptomButton = new ToolbarButton("Agregar Procedimiento", new ButtonListenerAdapter() {  
			 public void onClick(Button button, EventObject e) {  
				 //gridStore.load();
				 getEncounterGridWindow(labTestStore,gridStore,"Procedimientos","procedure-icon",false,false,false,true,false,false).show();

			 }  
		 });  
		 addSymptomButton.setIconCls("add-icon");
		 if(!edit){
			 addSymptomButton.setDisabled(true);
		 }
		 toolbar.addButton(addSymptomButton);  
		 toolbar.addSeparator();


		 final ToolbarButton deleteButton = new ToolbarButton("Eliminar", new ButtonListenerAdapter() {  
			 public void onClick(Button button, EventObject e) { 
				 gridStore.remove(getDeleteRecord());
				 button.setDisabled(true);
			 }  
		 });  


		 deleteButton.setIconCls("delete-icon");
		 deleteButton.setDisabled(true);
		 toolbar.addButton(deleteButton); 
		 toolbar.addSeparator();

		 final ToolbarButton refreshButton = new ToolbarButton("Refrescar", new ButtonListenerAdapter() {  
			 public void onClick(Button button, EventObject e) { 
				 gridStore.removeAll();
			 }  
		 });  
		 refreshButton.setIconCls("refresh-icon");
		 if(!edit){
			 refreshButton.setDisabled(true);
		 }
		 toolbar.addButton(refreshButton);

		 grid.setBottomToolbar(toolbar);

		 grid.addGridCellListener(new GridCellListener() {  


			 public void onCellDblClick(GridPanel grid, int rowIndex,
					 int colIndex, EventObject e) {
			 }


			 public void onCellClick(GridPanel grid, int rowIndex,
					 int colIndex, EventObject e) {
				 if(edit){
					 setDeleteRecord(grid.getStore().getAt(rowIndex));
					 deleteButton.setDisabled(false);
				 }
			 }


			 public void onCellContextMenu(GridPanel grid, int rowIndex,
					 int cellIndex, EventObject e) {
				 // TODO Auto-generated method stub

			 }
		 });  

		 return grid;
	 }
	 
	 
	 
	 public GridPanel getComplicationGrid(final Store gridStore,final boolean edit, ColumnModel colModel, final String title){

		 GridView view = new GridView();
		 view.setEmptyText("No hay Registros");
		 view.setAutoFill(true);
		 view.setForceFit(true);

		 GridPanel grid = new GridPanel(gridStore, colModel);
		 grid.setEnableDragDrop(false);
		 grid.setWidth(530);
		 grid.setHeight(300);
		 grid.setLoadMask(true);  
		 grid.setSelectionModel(new RowSelectionModel());  
		 grid.setFrame(true);  
		 grid.setView(view);
		 //grid.setTitle(title);


		 FieldDef[] gridFieldDefs = new FieldDef[] { 
				 new StringFieldDef("id"),
				 new StringFieldDef("name")
		 };

		 RecordDef gridRecordDef = new RecordDef(gridFieldDefs);
		 JsonReader gridReader = new JsonReader("response.value.items", gridRecordDef);
		 HttpProxy gridProxy = new HttpProxy("/semrs/complicationServlet", Connection.GET);
		 final Store labTestStore = new Store(gridProxy,gridReader,true);



		 Toolbar toolbar = new Toolbar();  
		 final ToolbarButton addSymptomButton = new ToolbarButton("Agregar Complicaci&oacute;n", new ButtonListenerAdapter() {  
			 public void onClick(Button button, EventObject e) {  
				 //gridStore.load();
				 getEncounterGridWindow(labTestStore,gridStore,"Complicaciones","complication-icon",false,false,false,true,false,false).show();

			 }  
		 });  
		 addSymptomButton.setIconCls("add-icon");
		 if(!edit){
			 addSymptomButton.setDisabled(true);
		 }
		 toolbar.addButton(addSymptomButton);  
		 toolbar.addSeparator();


		 final ToolbarButton deleteButton = new ToolbarButton("Eliminar", new ButtonListenerAdapter() {  
			 public void onClick(Button button, EventObject e) { 
				 gridStore.remove(getDeleteRecord());
				 button.setDisabled(true);
			 }  
		 });  


		 deleteButton.setIconCls("delete-icon");
		 deleteButton.setDisabled(true);
		 toolbar.addButton(deleteButton); 
		 toolbar.addSeparator();

		 final ToolbarButton refreshButton = new ToolbarButton("Refrescar", new ButtonListenerAdapter() {  
			 public void onClick(Button button, EventObject e) { 
				 gridStore.removeAll();
			 }  
		 });  
		 refreshButton.setIconCls("refresh-icon");
		 if(!edit){
			 refreshButton.setDisabled(true);
		 }
		 toolbar.addButton(refreshButton);

		 grid.setBottomToolbar(toolbar);

		 grid.addGridCellListener(new GridCellListener() {  


			 public void onCellDblClick(GridPanel grid, int rowIndex,
					 int colIndex, EventObject e) {
			 }


			 public void onCellClick(GridPanel grid, int rowIndex,
					 int colIndex, EventObject e) {
				 if(edit){
					 setDeleteRecord(grid.getStore().getAt(rowIndex));
					 deleteButton.setDisabled(false);
				 }
			 }


			 public void onCellContextMenu(GridPanel grid, int rowIndex,
					 int cellIndex, EventObject e) {
				 // TODO Auto-generated method stub

			 }
		 });  

		 return grid;
	 }
	 
	 
	 
	 
	 public GridPanel getDiagnosisGrid(final Store gridStore,final boolean edit, ColumnModel colModel, final String title){

		 GridView view = new GridView();
		 view.setEmptyText("No hay Registros");
		 view.setAutoFill(true);
		 view.setForceFit(true);

		 GridPanel grid = new GridPanel(gridStore, colModel);
		 grid.setEnableDragDrop(false);
		 grid.setWidth(530);
		 grid.setHeight(300);
		 grid.setLoadMask(true);  
		 grid.setSelectionModel(new RowSelectionModel());  
		 grid.setFrame(true);  
		 grid.setView(view);
		 //grid.setTitle(title);


		 FieldDef[] gridFieldDefs = new FieldDef[] { 
				 new StringFieldDef("id"),
				 new StringFieldDef("name")
		 };

		 RecordDef gridRecordDef = new RecordDef(gridFieldDefs);
		 JsonReader gridReader = new JsonReader("response.value.items", gridRecordDef);
		 HttpProxy gridProxy = new HttpProxy("/semrs/diseaseServlet", Connection.GET);
		 final Store labTestStore = new Store(gridProxy,gridReader,true);



		 Toolbar toolbar = new Toolbar();  
		 final ToolbarButton addSymptomButton = new ToolbarButton("Agregar Diagnostico", new ButtonListenerAdapter() {  
			 public void onClick(Button button, EventObject e) {  
				 //gridStore.load();
				 getEncounterGridWindow(labTestStore,gridStore,"Enfermedades","disease-icon",false,false,false,false,false,true).show();

			 }  
		 });  
		 addSymptomButton.setIconCls("add-icon");
		 if(!edit){
			 addSymptomButton.setDisabled(true);
		 }
		 toolbar.addButton(addSymptomButton);  
		 toolbar.addSeparator();


		 final ToolbarButton deleteButton = new ToolbarButton("Eliminar", new ButtonListenerAdapter() {  
			 public void onClick(Button button, EventObject e) { 
				 gridStore.remove(getDeleteRecord());
				 button.setDisabled(true);
			 }  
		 });  


		 deleteButton.setIconCls("delete-icon");
		 deleteButton.setDisabled(true);
		 toolbar.addButton(deleteButton); 
		 toolbar.addSeparator();

		 final ToolbarButton refreshButton = new ToolbarButton("Refrescar", new ButtonListenerAdapter() {  
			 public void onClick(Button button, EventObject e) { 
				 gridStore.removeAll();
			 }  
		 });  
		 refreshButton.setIconCls("refresh-icon");
		 if(!edit){
			 refreshButton.setDisabled(true);
		 }
		 toolbar.addButton(refreshButton);

		 grid.setBottomToolbar(toolbar);

		 grid.addGridCellListener(new GridCellListener() {  


			 public void onCellDblClick(GridPanel grid, int rowIndex,
					 int colIndex, EventObject e) {
			 }


			 public void onCellClick(GridPanel grid, int rowIndex,
					 int colIndex, EventObject e) {
				 if(edit){
					 setDeleteRecord(grid.getStore().getAt(rowIndex));
					 deleteButton.setDisabled(false);
				 }
			 }


			 public void onCellContextMenu(GridPanel grid, int rowIndex,
					 int cellIndex, EventObject e) {
				 // TODO Auto-generated method stub

			 }
		 });  

		 return grid;
	 }
	 
	 
	 public GridPanel getDrugGrid(final Store gridStore,final boolean edit, ColumnModel colModel, final String title){

		 GridView view = new GridView();
		 view.setEmptyText("No hay Registros");
		 view.setAutoFill(true);
		 view.setForceFit(true);

		 GridPanel grid = new GridPanel(gridStore, colModel);
		 grid.setEnableDragDrop(false);
		 grid.setWidth(530);
		 grid.setHeight(300);
		 grid.setLoadMask(true);  
		 grid.setSelectionModel(new RowSelectionModel());  
		 grid.setFrame(true);  
		 grid.setView(view);
		 //grid.setTitle(title);


		 FieldDef[] gridFieldDefs = new FieldDef[] { 
				 new StringFieldDef("id"),
				 new StringFieldDef("name")
		 };

		 RecordDef gridRecordDef = new RecordDef(gridFieldDefs);
		 JsonReader gridReader = new JsonReader("response.value.items", gridRecordDef);
		 HttpProxy gridProxy = new HttpProxy("/semrs/drugServlet", Connection.GET);
		 final Store labTestStore = new Store(gridProxy,gridReader,true);



		 Toolbar toolbar = new Toolbar();  
		 final ToolbarButton addSymptomButton = new ToolbarButton("Agregar Tratamiento", new ButtonListenerAdapter() {  
			 public void onClick(Button button, EventObject e) {  
				 //gridStore.load();
				 getEncounterGridWindow(labTestStore,gridStore,"Medicamentos","drugs-icon",false,false,false,false,true,false).show();

			 }  
		 });  
		 addSymptomButton.setIconCls("add-icon");
		 if(!edit){
			 addSymptomButton.setDisabled(true);
		 }
		 toolbar.addButton(addSymptomButton);  
		 toolbar.addSeparator();


		 final ToolbarButton deleteButton = new ToolbarButton("Eliminar", new ButtonListenerAdapter() {  
			 public void onClick(Button button, EventObject e) { 
				 gridStore.remove(getDeleteRecord());
				 button.setDisabled(true);
			 }  
		 });  


		 deleteButton.setIconCls("delete-icon");
		 deleteButton.setDisabled(true);
		 toolbar.addButton(deleteButton); 
		 toolbar.addSeparator();

		 final ToolbarButton refreshButton = new ToolbarButton("Refrescar", new ButtonListenerAdapter() {  
			 public void onClick(Button button, EventObject e) { 
				 gridStore.removeAll();
			 }  
		 });  
		 refreshButton.setIconCls("refresh-icon");
		 if(!edit){
			 refreshButton.setDisabled(true);
		 }
		 toolbar.addButton(refreshButton);

		 grid.setBottomToolbar(toolbar);

		 grid.addGridCellListener(new GridCellListener() {  


			 public void onCellDblClick(GridPanel grid, int rowIndex,
					 int colIndex, EventObject e) {
			 }


			 public void onCellClick(GridPanel grid, int rowIndex,
					 int colIndex, EventObject e) {
				 if(edit){
					 setDeleteRecord(grid.getStore().getAt(rowIndex));
					 deleteButton.setDisabled(false);
				 }
			 }


			 public void onCellContextMenu(GridPanel grid, int rowIndex,
					 int cellIndex, EventObject e) {
				 // TODO Auto-generated method stub

			 }
		 });  

		 return grid;
	 }
	 
	 
	 
	 public Window getEncounterGridWindow(final Store from, final Store to, 
			   String title, String iconClass, final boolean isSymptom, final boolean isDisease,
			   final boolean isLabTest, final boolean isProcedureOrComp, final boolean isDrug, final boolean isDiagnosis){

	    	final Window gridWindow =  new com.gwtext.client.widgets.Window();  

	    	gridWindow.setTitle(title);  
	    	gridWindow.setWidth(750);  
	    	gridWindow.setHeight(610);    
	    	gridWindow.setLayout(new FitLayout());  
	    	gridWindow.setPaddings(5);  
	    	gridWindow.setResizable(false);
	    	gridWindow.setButtonAlign(Position.CENTER);  
	    	gridWindow.setModal(true);
	    	gridWindow.setIconCls(iconClass);
	    	gridWindow.setCloseAction(com.gwtext.client.widgets.Window.HIDE);  
	    	ResizableConfig config = new ResizableConfig();  
		    config.setHandles(Resizable.ALL);  
	    	final Resizable resizable = new Resizable(gridWindow, config);  
	        resizable.addListener(new ResizableListenerAdapter() {  
	              public void onResize(Resizable self, int width, int height) {  
	            	  gridWindow.setWidth(width);  
	            	  gridWindow.setHeight(height);
	           	   
	              }  
	         });  
	    	

	    	final FormPanel gridForm = new FormPanel(); 
	    	gridForm.setFrame(true);  
	    	gridForm.setWidth(750);  
	    	gridForm.setHeight(550);
	    	gridForm.setAutoScroll(true);
	    	gridForm.setPaddings(5, 5, 5, 0);  
	    	gridForm.setLabelAlign(Position.TOP); 
	    	
	    	
	    	Panel topPanel = new Panel();  
	    	topPanel.setLayout(new ColumnLayout());  

	    	//create first panel and add fields to it  
	    	Panel columnOnePanel = new Panel();  
	    	columnOnePanel.setLayout(new FormLayout());  

	    	TextField id = new TextField("C&oacute;digo", "id");
	    	columnOnePanel.add(id, new AnchorLayoutData("65%"));  

	    	topPanel.add(columnOnePanel, new ColumnLayoutData(.5));  

	    	Panel columnTwoPanel = new Panel();  
	    	columnTwoPanel.setLayout(new FormLayout());  

	    	TextField labTestName = new TextField("Nombre", "name");  
	    	columnTwoPanel.add(labTestName, new AnchorLayoutData("65%")); 
	    	topPanel.add(columnTwoPanel, new ColumnLayoutData(0.5));  

	    	Store severityStore = new SimpleStore(new String[]{"id", "desc"}, new String[][]{ 
	    			new String[]{"Baja","Baja"}, new String[]{"Media", "Media"},
	    			new String[]{"Alta","Alta"}, new String[]{"Muy Alta", "Muy Alta"}});  
	    	severityStore.load();  
	    	final ComboBox severityCB = new ComboBox();  
	    	severityCB.setFieldLabel("Severidad");  
	    	severityCB.setHiddenName("severity");  
	    	severityCB.setStore(severityStore);  
	    	severityCB.setDisplayField("desc"); 
	    	severityCB.setMode(ComboBox.LOCAL);  
	    	severityCB.setTriggerAction(ComboBox.ALL);  
	    	severityCB.setSelectOnFocus(true);  
	    	severityCB.setWidth(190);
	    	severityCB.setEditable(false);
	    	severityCB.setName("severity");
	    	severityCB.setReadOnly(true);
	    	severityCB.setValueField("id");
	    	severityCB.setValue("Baja");

	    	
	    	
	    	Store testResultStore = new SimpleStore(new String[]{"id", "desc"}, new String[][]{ 
	    			new String[]{"Positivo","Positivo"}, new String[]{"Negativo", "Negativo"}});  
	    	testResultStore.load();  
	    	final ComboBox testResultCB = new ComboBox();  
	    	testResultCB.setFieldLabel("Resultado");  
	    	testResultCB.setHiddenName("testResult");  
	    	testResultCB.setStore(testResultStore);  
	    	testResultCB.setDisplayField("desc"); 
	    	testResultCB.setMode(ComboBox.LOCAL);  
	    	testResultCB.setTriggerAction(ComboBox.ALL);  
	    	testResultCB.setSelectOnFocus(true);  
	    	testResultCB.setWidth(190);
	    	testResultCB.setEditable(false);
	    	testResultCB.setName("testResult");
	    	testResultCB.setReadOnly(true);
	    	testResultCB.setValueField("id");
	    	testResultCB.setValue("Positivo");
	    	
	    	final TextArea labTestResultsDesc = new TextArea("Observaciones", "resultDesc"); 
	    	labTestResultsDesc.setHideLabel(false);  
	    	labTestResultsDesc.setWidth(280);
	    	labTestResultsDesc.setHeight(90);
	    	
	    	final DateField testDate = new DateField("Fecha","testDate", 140);
	    	testDate.setMaxValue(new Date());
	    	testDate.setFormat("d/m/Y");
	    	
	    	/*Drugs*/
	    	final DateField startDate = new DateField("Fecha Inicio","startDate", 140);
	    	startDate.setMinValue(new Date());
	    	startDate.setFormat("d/m/Y");
	    	
	    	
	    	final DateField endDate = new DateField("Fecha Fin","endDate", 140);
	    	endDate.setMinValue(new Date());
	    	endDate.setFormat("d/m/Y");
	    	
	    	final TextArea instructions = new TextArea("Instrucciones", "instructions"); 
	    	instructions.setHideLabel(false);  
	    	instructions.setWidth(280);
	    	instructions.setHeight(90);
	    	/**/
	    	
	    	/*Diagnoses*/
	    	
	    	Store typeStore = new SimpleStore(new String[]{"id", "desc"}, new String[][]{ 
	    			new String[]{"","No Aplica"},new String[]{"Aguda","Aguda"}, new String[]{"Cronica", "Cr&oacute;nica"},
	    			new String[]{"Recurrente","Recurrente"}});  
	    	typeStore.load();  
	    	final ComboBox typeCB = new ComboBox();  
	    	typeCB.setFieldLabel("Tipo");  
	    	typeCB.setHiddenName("diagnosisType");  
	    	typeCB.setStore(typeStore);  
	    	typeCB.setDisplayField("desc"); 
	    	typeCB.setMode(ComboBox.LOCAL);  
	    	typeCB.setTriggerAction(ComboBox.ALL);  
	    	typeCB.setSelectOnFocus(true);  
	    	typeCB.setWidth(190);
	    	typeCB.setEditable(false);
	    	typeCB.setName("diagnosisType");
	    	typeCB.setReadOnly(true);
	    	typeCB.setValueField("id");
	    	typeCB.setBlankText("No Aplica");
	    	
	    	
	    	
	    	/**/
	    	
	    	
	    	FieldSet fieldSet = new FieldSet();
	    	fieldSet.add(topPanel);


	    	Panel proxyPanel = new Panel();  
	    	proxyPanel.setBorder(true);  
	    	proxyPanel.setBodyBorder(false);
	    	proxyPanel.setCollapsible(false);  
	    	proxyPanel.setLayout(new FormLayout());
	    	proxyPanel.setButtonAlign(Position.CENTER);

	    	Button clear = new Button("Limpiar");
	    	clear.setIconCls("clear-icon");
	    	clear.addListener(new ButtonListenerAdapter(){

	    		public void onClick(Button button, EventObject e){
	    			gridForm.getForm().reset();

	    		}

	    	});
	    	proxyPanel.addButton(clear);  

	    	final PagingToolbar pagingToolbar = new PagingToolbar(from); 
	    	pagingToolbar.setPageSize(20);
	    	pagingToolbar.setDisplayInfo(true);
	    	pagingToolbar.setEmptyMsg("No hay registros");
	    	pagingToolbar.setDisplayMsg("Mostrando Registros {0} - {1} de {2}");
	    	from.load(0, pagingToolbar.getPageSize());
	    	MainPanel.resetTimer();
	    	
	    	final ToolbarButton button = new ToolbarButton("Agregar", new ButtonListenerAdapter() {  
	    		public void onClick(Button button, EventObject e) { 
	    			Record record = getGridWindowRecord();
	    			if(isSymptom||isDisease){
	    			  record.set("severity", severityCB.getValue());
	    			}else if(isLabTest){
	    				 record.set("result", testResultCB.getValue());
	    				 record.set("resultDesc", labTestResultsDesc.getValueAsString().replace('@',' '));
	    				 record.set("date", com.gwtext.client.util.DateUtil.format(testDate.getValue(), "d/m/Y"));	
	    			}else if(isProcedureOrComp){
	    				 record.set("date",com.gwtext.client.util.DateUtil.format(testDate.getValue(), "d/m/Y"));
	    			}else if(isDrug){
	    				 record.set("startDate",com.gwtext.client.util.DateUtil.format(startDate.getValue(), "d/m/Y"));
	    				 record.set("endDate",com.gwtext.client.util.DateUtil.format(endDate.getValue(), "d/m/Y"));
	    				 record.set("instructions",instructions.getValueAsString().replace('@',' '));
	    			}else if(isDiagnosis){
	    				record.set("severity", severityCB.getValue());
	    				record.set("type", typeCB.getValue());
	    			}
	    			if(to.getRecords()!=null && to.getRecords().length>0){
	    			for(int i =0; i<to.getRecords().length;++i){  			
	    				Record toRecord = to.getRecords()[i];
    					if(isSymptom||isDisease){
    						if(record.getAsString("id").equals(toRecord.getAsString("id")) &&
    								record.getAsString("htype").equals(toRecord.getAsString("htype")) ){
    	    	    			from.remove(getGridWindowRecord());
    	    					button.setDisabled(true);
    	    					break;
    	    				}else if(i==to.getRecords().length-1){	    					
    	    					to.add(record);			
    	    	    			from.remove(record);
    	    	    			button.setDisabled(true);		
    	    				}
    						
    					}else{
    						if(record.getAsString("id").equals(toRecord.getAsString("id"))){
    	    	    			from.remove(record);
    	    					button.setDisabled(true);
    	    					break;
    	    				}else if(i==to.getRecords().length-1){
    	    					//setGridWindowRecord(record);
    	    					to.add(record);			
    	    	    			from.remove(record);
    	    	    			button.setDisabled(true);		
    	    				}
    						
    					}
	    			
	    			}
	    			
	    			}else{
	    				to.add(record);
		    			from.remove(record);
		    			button.setDisabled(true);
	    			}
	    		  } 
	    	});  
	    	from.addStoreListener(new StoreListenerAdapter() {
	    	       	public void onLoadException(Throwable error) {
	    	 	      // MessageBox.alert("Error", "Ha ocurrido un error al tratar de obtener la lista de registros.");
	    	 	      MessageBox.show(new MessageBoxConfig() {  
      					{  
      						setTitle("Error");
      						setMsg("Ha ocurrido un error al tratar de obtener la lista de registros.");
      						setIconCls(MessageBox.ERROR);
      					    setModal(true);
      					    setButtons(MessageBox.OK);
      					}  
      				});
	    			}
	    		
	    	});
	    	button.setIconCls("add-icon");
	    	button.setDisabled(true);
	    	pagingToolbar.addButton(button);  

	    	final Button search = new Button("Buscar");  
	    	search.setIconCls("search-icon");  
	    	search.addListener(new ButtonListenerAdapter(){

	    		public void onClick(Button button, EventObject e){
	    			UrlParam[] params = getFormData(gridForm.getForm());
	    			from.setBaseParams(params);
	    			from.load(0, pagingToolbar.getPageSize());
	    			pagingToolbar.updateInfo();
	    			MainPanel.resetTimer();
	    		}

	    	});
	    	proxyPanel.addButton(search);  
	    	fieldSet.add(proxyPanel);
	    	gridForm.add(fieldSet); 
	    	if(isSymptom || isDisease){
	    		FieldSet cbFS = new FieldSet();
	    		cbFS.add(severityCB);
	    		gridForm.add(cbFS);
	    	}else if(isLabTest){
	    		FieldSet testFieldSet = new FieldSet();
	    		testFieldSet.add(testResultCB);
	    		testFieldSet.add(testDate);
	    		testFieldSet.add(labTestResultsDesc);
	    		gridForm.add(testFieldSet);
	    	}else if(isProcedureOrComp){
	    		FieldSet dateFieldSet = new FieldSet();
	    		dateFieldSet.add(testDate);
	    		gridForm.add(dateFieldSet);
	    	}else if(isDrug){
	     		FieldSet drugFieldSet = new FieldSet();
	     		drugFieldSet.add(startDate);
	     		drugFieldSet.add(endDate);
	     		drugFieldSet.add(instructions);
	    		gridForm.add(drugFieldSet);
	    	}else if(isDiagnosis){
	    		FieldSet cbFS = new FieldSet();
	    		cbFS.add(severityCB);
	    		cbFS.add(typeCB);
	    		gridForm.add(cbFS);	
	    	}
	    	
	    	
	    	// formPanel.add(proxyPanel);

	    	gridForm.setMonitorValid(true);
	    	gridForm.addListener(new FormPanelListenerAdapter() {
	    		public void onClientValidation(FormPanel formPanel, boolean valid) {
	    			search.setDisabled(!valid);
	    		}
	    	});


	    	GridView view = new GridView();
	    	view.setEmptyText("No hay Registros");
	    	view.setAutoFill(true);
	    	view.setForceFit(true);





	    	GridPanel grid = new GridPanel(from,createColModel());
	    	grid.setEnableDragDrop(false);
	    	grid.setWidth(705);
	    	grid.setHeight(350);
	    	grid.setLoadMask(true);  
	    	grid.setSelectionModel(new RowSelectionModel());  
	    	grid.setFrame(true);  
	    	grid.setView(view);
	    	grid.setTitle(title);
	    	grid.addGridCellListener(new GridCellListener() {  


	    		public void onCellDblClick(GridPanel grid, int rowIndex,
	    				int colIndex, EventObject e) {
	    		}


	    		public void onCellClick(GridPanel grid, int rowIndex,
	    				int colIndex, EventObject e) {
	    			Record record = grid.getStore().getAt(rowIndex);
	    				if(isSymptom){
							record.set("type", "S&iacute;ntoma");
    						record.set("htype", "Symptom");
    					}else if(isDisease){
    						record.set("type", "Enfermedad");
    						record.set("htype", "Disease");
    					}

    				//record.set("severity", severityCB.getValue());
	    			setGridWindowRecord(record);
	    			button.setDisabled(false);

	    		}


	    		public void onCellContextMenu(GridPanel grid, int rowIndex,
	    				int cellIndex, EventObject e) {
	    			// TODO Auto-generated method stub

	    		}
	    	});  
	    	grid.setBottomToolbar(pagingToolbar);
	    	
	    	
	    	
	    	gridForm.add(grid);
	    	gridWindow.add(gridForm);

	    	return gridWindow;
	    }

	  public String getRecordValues(Record[] records, boolean isSymptomOrDisease, boolean isLabTest, 
			  boolean isProcedureOrComp, boolean isDrug, boolean isDiagnosis){

			String recordValues = "";

			
			if(isSymptomOrDisease){
				for(int i = 0 ; i<records.length; i++){	
					if(i<records.length-1){
						recordValues += records[i].getAsString("id")+"@"+ records[i].getAsString("name")+"@"+ records[i].getAsString("htype")+"@"+ records[i].getAsString("severity") + ",";
					}else{
						recordValues += records[i].getAsString("id")+"@"+ records[i].getAsString("name")+"@"+ records[i].getAsString("htype")+"@"+ records[i].getAsString("severity");
					}
				}
			}else if(isLabTest){
				for(int i = 0 ; i<records.length; i++){
					if(i<records.length-1){
						recordValues += records[i].getAsString("id")+"@"+ records[i].getAsString("name")+"@"+ records[i].getAsString("result")+"@"+ records[i].getAsString("resultDesc")+"@"+ records[i].getAsString("date") + ",";
					}else{
						recordValues += records[i].getAsString("id")+"@"+ records[i].getAsString("name")+"@"+ records[i].getAsString("result")+"@"+ records[i].getAsString("resultDesc")+"@"+ records[i].getAsString("date");
					}
				}
			}else if(isProcedureOrComp){
				for(int i = 0 ; i<records.length; i++){
					if(i<records.length-1){
						recordValues += records[i].getAsString("id")+"@"+ records[i].getAsString("name")+"@"+records[i].getAsString("date")+",";
					}else{
						recordValues += records[i].getAsString("id")+"@"+ records[i].getAsString("name")+"@"+records[i].getAsString("date");
					}
				}
			}else if(isDrug){
				for(int i = 0 ; i<records.length; i++){
					if(i<records.length-1){
						recordValues += records[i].getAsString("id")+"@"+ records[i].getAsString("name")+"@"+records[i].getAsString("instructions")+
						"@"+records[i].getAsString("startDate")+"@"+records[i].getAsString("endDate")+",";
					}else{
						recordValues += records[i].getAsString("id")+"@"+ records[i].getAsString("name")+"@"+records[i].getAsString("instructions")+
						"@"+records[i].getAsString("startDate")+"@"+records[i].getAsString("endDate");
					}
				}
			}else if(isDiagnosis){
				for(int i = 0 ; i<records.length; i++){
					if(i<records.length-1){
						recordValues += records[i].getAsString("id")+"@"+ records[i].getAsString("name")+"@"+records[i].getAsString("severity")+"@"+records[i].getAsString("type")+",";
					}else{
						recordValues += records[i].getAsString("id")+"@"+ records[i].getAsString("name")+"@"+records[i].getAsString("severity")+"@"+records[i].getAsString("type");
					}
				  }
			  }

			return recordValues;

		}
	  
	  
	  public Window getDiagnosisGridWindow(Store diagStore, Store predictionStore){
		  
			final Window gridWindow =  new com.gwtext.client.widgets.Window();  
	    	gridWindow.setTitle("Diagn&oacute;stico Diferencial Asistido");  
	    	gridWindow.setWidth(780);  
	    	gridWindow.setHeight(600);    
	    	gridWindow.setLayout(new FitLayout());  
	    	gridWindow.setPaddings(5);  
	    	gridWindow.setResizable(false);
	    	gridWindow.setDraggable(true);
	    	gridWindow.setButtonAlign(Position.CENTER);  
	    	gridWindow.setModal(false);
	    	gridWindow.setIconCls("disease-icon");
	    	gridWindow.setCloseAction(com.gwtext.client.widgets.Window.HIDE);  
	    
	    	ResizableConfig config = new ResizableConfig();  
		    config.setHandles(Resizable.ALL);  
	    	final Resizable resizable = new Resizable(gridWindow, config);  
	        resizable.addListener(new ResizableListenerAdapter() {  
	              public void onResize(Resizable self, int width, int height) {  
	            	  gridWindow.setWidth(width);  
	            	  gridWindow.setHeight(height);
	           	   
	              }  
	         });  
	    	

	    	final FormPanel gridForm = new FormPanel(); 
	    	gridForm.setFrame(true);  
	    	gridForm.setWidth(750);  
	    	gridForm.setHeight(550);
	    	gridForm.setAutoScroll(true);
	    	gridForm.setPaddings(5, 5, 5, 0);  
	    	gridForm.setLabelAlign(Position.TOP); 
	    	
	    	
	    	
	    	final PieChart chart = new PieChart();  
	    	chart.setTitle("Diagn&oacute;stico en base a la base de conocimientos");  
	    	chart.setWMode("transparent");  
	    	chart.setStore(diagStore);  
	    	chart.setDataField("probability");  
	    	chart.setCategoryField("name");  
	    	chart.setFrame(true);
	   
	 
	    	chart.setExpressInstall("js/yui/assets/expressinstall.swf");  
	    	chart.setWidth(705);  
	    	chart.setHeight(400);  
	    	
	    	MainPanel.resetTimer();


	    	GridView view = new GridView();
	    	view.setEmptyText("No hay Registros");
	    	view.setAutoFill(true);
	    	view.setForceFit(true);
	    	
	    	 Renderer renderer = new Renderer() {  
	             public String render(Object value, CellMetadata cellMetadata, Record record, int rowIndex, int colNum, Store store) {  
	                 //NumberFormat nf = NumberFormat.getPercentFormat();
	                 //return nf.format(((Double) value).doubleValue());  
	            	 return value + "%";
	             }  
	         };  


	        ColumnConfig probConfig = new ColumnConfig("Probabilidad", "probability");  
	        probConfig.setRenderer(renderer);  
	  

    		ColumnModel colModel = new ColumnModel(new ColumnConfig[] { 
    	      		new ColumnConfig("C&oacute;digo", "id"),
    	            new ColumnConfig("Nombre", "name"),
    	            //new ColumnConfig("% Probabilidad", "probability")
    	            probConfig
    	      		//probabilityConfig
    		       });
    	           // legendConfig});
    		 for (int i = 0; i < colModel.getColumnConfigs().length; i++){
    	          ((ColumnConfig) colModel.getColumnConfigs()[i]).setSortable(true);
    	      }
    		


	    	GridPanel grid = new GridPanel(diagStore,colModel);
	    	grid.setEnableDragDrop(false);
	    	grid.setWidth(705);
	    	grid.setHeight(350);
	    	grid.setLoadMask(true);  
	    	grid.setSelectionModel(new RowSelectionModel());  
	    	grid.setFrame(true);  
	    	grid.setView(view);
	    	grid.setTitle("Enfermedades Encontradas");
	    	grid.addGridCellListener(new GridCellListener() {  
	    		 public void onCellDblClick(GridPanel grid, int rowIndex,
						 int colIndex, EventObject e) {
					 Record r = grid.getStore().getAt(rowIndex);
					 String recordId = r.getAsString("id");
					 com.gwtext.client.widgets.Window diseaseWindow = getEditDiseaseWindow(recordId);
					 diseaseWindow.show();
				 }


				 public void onCellClick(GridPanel grid, int rowIndex,
						 int colIndex, EventObject e) {
					 final Record r = grid.getStore().getAt(rowIndex);
					 String recordId = r.getAsString("id");

				 }


				 public void onCellContextMenu(GridPanel grid, int rowIndex,
						 int cellIndex, EventObject e) {
					 // TODO Auto-generated method stub

				 }
			 });  
	    	
	    	
	    	

	    	final PieChart chartP = new PieChart();  
	    	chartP.setTitle("Diagn&oacute;stico en base a casos similares");  
	    	chartP.setWMode("transparent");  
	    	chartP.setStore(predictionStore);  
	    	chartP.setDataField("probability");  
	    	chartP.setCategoryField("name");  
	    	chartP.setFrame(true);
	    	//chart.setCollapsible(true);
	    	//chart.setAnimCollapse(true);
	    	
	 
	    	chartP.setExpressInstall("js/yui/assets/expressinstall.swf");  
	    	chartP.setWidth(705);  
	    	chartP.setHeight(400);  
	
	    
	    	
	    

	    	GridView viewP = new GridView();
	    	viewP.setEmptyText("No hay Registros");
	    	viewP.setAutoFill(true);
	    	viewP.setForceFit(true);


	    	GridPanel gridP = new GridPanel(predictionStore,colModel);
	    	gridP.setEnableDragDrop(false);
	    	gridP.setWidth(705);
	    	gridP.setHeight(350);
	    	gridP.setLoadMask(true);  
	    	gridP.setSelectionModel(new RowSelectionModel());  
	    	gridP.setFrame(true);  
	    	gridP.setView(viewP);
	    	gridP.setTitle("Enfermedades Encontradas");
	    	gridP.addGridCellListener(new GridCellListener() {  
	    		 public void onCellDblClick(GridPanel grid, int rowIndex,
						 int colIndex, EventObject e) {
					 Record r = grid.getStore().getAt(rowIndex);
					 String recordId = r.getAsString("id");
					 com.gwtext.client.widgets.Window diseaseWindow = getEditDiseaseWindow(recordId);
					 diseaseWindow.show();
				 }


				 public void onCellClick(GridPanel grid, int rowIndex,
						 int colIndex, EventObject e) {

				 }


				 public void onCellContextMenu(GridPanel grid, int rowIndex,
						 int cellIndex, EventObject e) {
					 // TODO Auto-generated method stub

				 }
			 });
	    	
	    	
	    	
	    	Panel firstColumn = new Panel();  
		    firstColumn.setLayout(new FormLayout());  
		    firstColumn.setBorder(false);
		    //firstColumn.setFrame(true);
		    
			
			firstColumn.add(chart,new AnchorLayoutData("100%") );
			firstColumn.add(grid,new AnchorLayoutData("100%") );  
	
			
			Panel secondColumn = new Panel();  
			secondColumn.setLayout(new FormLayout());  
			secondColumn.setBorder(false); 
			//secondColumn.setFrame(true);

			secondColumn.add(chartP,new AnchorLayoutData("100%") );
			secondColumn.add(gridP,new AnchorLayoutData("100%") );

			
			Panel columnPanel = new Panel();
			columnPanel.setLayout(new ColumnLayout());

			columnPanel.add(firstColumn, new ColumnLayoutData(0.5));
			columnPanel.add(secondColumn, new ColumnLayoutData(0.5));
	    	
	    	
	    	//diagStore.load();
	    	gridForm.add(columnPanel);
	    	gridWindow.add(gridForm);


	    	return gridWindow;
		  
		  
	  }
	  
	  
	  
	  public com.gwtext.client.widgets.Window getEditDiseaseWindow(String diseaseId){
			 
		  final Window editDiseaseWindow = new com.gwtext.client.widgets.Window();  
			 editDiseaseWindow.setTitle("Detalle de Enfermedad");  
			 editDiseaseWindow.setWidth(700);  
			 editDiseaseWindow.setHeight(600);    
			 editDiseaseWindow.setLayout(new FitLayout());  
			 editDiseaseWindow.setPaddings(5);  
			 editDiseaseWindow.setResizable(true);
			 editDiseaseWindow.setButtonAlign(Position.CENTER);  
			 editDiseaseWindow.setModal(false);
			 editDiseaseWindow.setDraggable(true);
			 editDiseaseWindow.setIconCls("disease-icon");
			 editDiseaseWindow.setCloseAction(com.gwtext.client.widgets.Window.HIDE);  
			 editDiseaseWindow.setMaximizable(true);
			 //editDiseaseWindow.setMinimizable(true);

			 RecordDef recordDef = new RecordDef(new FieldDef[]{   
					 new StringFieldDef("id"),  
					 new StringFieldDef("name"),  
					 new StringFieldDef("description"),
					 new StringFieldDef("sex"),
					 new StringFieldDef("ages"),
					 new StringFieldDef("toxicHabits"),
					 new StringFieldDef("loadSuccess")
			 });  

			 final JsonReader reader = new JsonReader("data", recordDef);  
			 reader.setSuccessProperty("success"); 
			 reader.setId("id");

			 //setup error reader to process from submit response from server  
			 RecordDef errorRecordDef = new RecordDef(new FieldDef[]{  
					 new StringFieldDef("id"),  
					 new StringFieldDef("msg")  
			 });  

			 final JsonReader errorReader = new JsonReader("field", errorRecordDef);  
			 errorReader.setSuccessProperty("success"); 

			

			 FormPanel diseaseForm = new FormPanel(); 

			 diseaseForm.setReader(reader);  
			 diseaseForm.setErrorReader(errorReader); 
			 diseaseForm.setFrame(true);  
			 diseaseForm.setWidth(700);  
			 diseaseForm.setHeight(600);
			 diseaseForm.setAutoScroll(true);

			 Panel proxyPanel = new Panel();  
			 proxyPanel.setBorder(true);  
			 proxyPanel.setBodyBorder(false);
			 proxyPanel.setCollapsible(false);  
			 proxyPanel.setLayout(new FormLayout());
			 proxyPanel.setButtonAlign(Position.CENTER);
			 //proxyPanel.setIconCls("groupProxyPanel");

			 FieldSet diseaseFS = new FieldSet("Informaci&oacute;n de Enfermedad");  
			 diseaseFS.setCollapsible(true);
			 diseaseFS.setFrame(false);  

			 TextField diseaseIdText = new TextField("C&oacute;digo","id",190);
			 diseaseIdText.setReadOnly(true);
		
			 diseaseIdText.setAllowBlank(false);
			 diseaseIdText.setStyle("textTransform: uppercase;");
			 diseaseIdText.addListener(new FieldListenerAdapter(){
				 public void onBlur(Field field) {
					 String value = field.getValueAsString();
					 field.setValue(value.toUpperCase());
				 }

			 });
			 diseaseFS.add(diseaseIdText);  
			 
			 TextField loadSuccess = new TextField("loadSuccess","loadSuccess",190);
		     loadSuccess.setVisible(false);
		     diseaseFS.add(loadSuccess); 

			 TextField diseaseNameText = new TextField("Nombre", "name", 190);  ;
			 diseaseNameText.setAllowBlank(false);
			 diseaseFS.add(diseaseNameText); 
			 
			//add a ComboBox field  
			 Store sexStore = new SimpleStore(new String[]{"abbr", "sex"}, new String[][]{ new String[]{"M","Masculino"}, new String[]{"F", "Femenino"}});  
			 sexStore.load();  

			 final ComboBox sexCB = new ComboBox();  
			 sexCB.setFieldLabel("Sexo");  
			 sexCB.setHiddenName("sex");  
			 sexCB.setStore(sexStore);  
			 sexCB.setDisplayField("sex");  
			 sexCB.setTypeAhead(true);  
			 sexCB.setMode(ComboBox.LOCAL);  
			 sexCB.setTriggerAction(ComboBox.ALL);  
			 sexCB.setDisabled(true);
			 sexCB.setWidth(190); 
			 sexCB.setEmptyText("Indiferente");
			 sexCB.setName("sex"); 
			 diseaseFS.add(sexCB); 
			 
				//add a ComboBox field  
			 Store agesStore = new SimpleStore(new String[]{"agesid", "ages"}, 
					 new String[][]{ new String[]{"0-10","0-10"}, new String[]{"10-20", "10-20"},
					 new String[]{"20-30", "20-30"},new String[]{"30-40", "30-40"},
					 new String[]{"40-50", "40-50"},new String[]{"50-60", "50-60"},
					 new String[]{"60 +", "60 +"}});  
			 agesStore.load();  

			 final ComboBox agesCB = new ComboBox();  
			 agesCB.setFieldLabel("Rango de Edad");  
			 agesCB.setHiddenName("ages");  
			 agesCB.setStore(agesStore);  
			 agesCB.setDisplayField("ages");  
			 agesCB.setTypeAhead(true);  
			 agesCB.setMode(ComboBox.LOCAL);  
			 agesCB.setTriggerAction(ComboBox.ALL);  
			 agesCB.setDisabled(true);
			 agesCB.setWidth(190); 
			 agesCB.setEmptyText("Indiferente");
			 agesCB.setName("ages"); 
			 diseaseFS.add(agesCB); 
			 
			 
			 Store toxicHabitsStore = new SimpleStore(new String[]{"toxicHabitValue", "toxicHabits"}, new String[][]{ new String[]{"Si","Si"}, new String[]{"No", "No"}});  
			 toxicHabitsStore.load();  

			 final ComboBox toxicHabitsCB = new ComboBox();  
			 toxicHabitsCB.setFieldLabel("H&aacute;bitos T&oacute;xicos");  
			 toxicHabitsCB.setHiddenName("toxicHabits");  
			 toxicHabitsCB.setStore(toxicHabitsStore);  
			 toxicHabitsCB.setDisplayField("toxicHabits");  
			 toxicHabitsCB.setTypeAhead(true);  
			 toxicHabitsCB.setMode(ComboBox.LOCAL);  
			 toxicHabitsCB.setTriggerAction(ComboBox.ALL);  
			 toxicHabitsCB.setDisabled(true); 
			 toxicHabitsCB.setWidth(190); 
			 toxicHabitsCB.setName("toxicHabits"); 
			 diseaseFS.add(toxicHabitsCB);


			 HtmlEditor diseaseDesc = new HtmlEditor("Descripci&oacute;n", "description");  
			 // drugDesc.setWidth(190);
			 diseaseDesc.setHeight(200);  
			 diseaseFS.add(diseaseDesc); 

			 diseaseForm.add(diseaseFS);

			 FieldSet diseaseSymptomsFS = new FieldSet("S&iacute;ntomas");  
			 diseaseSymptomsFS.setCollapsible(true);
			 diseaseSymptomsFS.setFrame(false);  
			 
			 FieldSet diseaseLabTestsFS = new FieldSet("Ex&aacute;menes de Laboratorio");
			 diseaseLabTestsFS.setCollapsible(true);
			 diseaseLabTestsFS.setFrame(false);  
			 
			 FieldSet diseaseProceduresFS = new FieldSet("Procedimientos"); 
			 diseaseProceduresFS.setCollapsible(true);
			 diseaseProceduresFS.setFrame(false);  
			 
			 FieldSet relatedDiseasesFS = new FieldSet("Enfermedades");
			 relatedDiseasesFS.setCollapsible(true);
			 relatedDiseasesFS.setFrame(false);  

			   /*Symptoms*/
			   FieldDef[] gridFieldDefs = new FieldDef[] { 
			     		new StringFieldDef("id"),
			            new StringFieldDef("name")
			     };

			    RecordDef gridRecordDef = new RecordDef(gridFieldDefs);
			    JsonReader gridReader = new JsonReader("response.value.items", gridRecordDef);
			    HttpProxy gridProxy = new HttpProxy("/semrs/diseaseServlet?diseaseEdit=getSymptoms&diseaseId="+diseaseId+"&diseaseSymptoms=true", Connection.GET);
			    final Store gridStore = new Store(gridProxy,gridReader,true);

				
		  		
		  		/*LabTests*/
		  		FieldDef[] gridFieldDefsL = new FieldDef[] { 
			     		new StringFieldDef("id"),
			            new StringFieldDef("name")
			     };

			    RecordDef gridRecordDefL = new RecordDef(gridFieldDefsL);
			    JsonReader gridReaderL = new JsonReader("response.value.items", gridRecordDefL);
			    HttpProxy gridProxyL = new HttpProxy("/semrs/diseaseServlet?diseaseEdit=getLabTests&diseaseId="+diseaseId+"&diseaseLabTests=true", Connection.GET);
			    final Store gridStoreL = new Store(gridProxyL,gridReaderL,true);
			   

				  
		  		
		  		/*Procedures*/
		        FieldDef[] gridFieldDefsP = new FieldDef[] { 
			     		new StringFieldDef("id"),
			            new StringFieldDef("name")
			     };

			    RecordDef gridRecordDefP = new RecordDef(gridFieldDefsP);
			    JsonReader gridReaderP = new JsonReader("response.value.items", gridRecordDefP);
			    HttpProxy gridProxyP = new HttpProxy("/semrs/diseaseServlet?diseaseEdit=getProcedures&diseaseId="+diseaseId+"&diseaseProcedures=true", Connection.GET);
			    final Store gridStoreP = new Store(gridProxyP,gridReaderP,true);
			   
			  
			 
		  		
		  		/*Diseases*/
		  		FieldDef[] gridFieldDefsD = new FieldDef[] { 
			     		new StringFieldDef("id"),
			            new StringFieldDef("name")
			     };

			    RecordDef gridRecordDefD = new RecordDef(gridFieldDefsD);
			    JsonReader gridReaderD = new JsonReader("response.value.items", gridRecordDefD);
			    HttpProxy gridProxyD = new HttpProxy("/semrs/diseaseServlet?diseaseEdit=getRelatedDiseases&diseaseId="+diseaseId+"&relatedDiseases=true", Connection.GET);
			    final Store gridStoreD = new Store(gridProxyD,gridReaderD,true);
			   

			    diseaseSymptomsFS.setCollapsed(true);
			    diseaseLabTestsFS.setCollapsed(true);
			    diseaseProceduresFS.setCollapsed(true);
			    relatedDiseasesFS.setCollapsed(true);
			    
	
			    
			    diseaseSymptomsFS.addListener(new PanelListenerAdapter(){
			    	public void onExpand(Panel panel) {
			    	   //if(sExpand){
			    		gridStore.load();
			    		//sExpand = false;
			  
			    		//panel.expand(true);
			        }
			    	
			    });
			    
			    diseaseLabTestsFS.addListener(new PanelListenerAdapter(){
			    	public void onExpand(Panel panel) {
			    		//if(ltExpand){
			    		  gridStoreL.load();
			
			    		//panel.expand(true);
			        }
			    	
			    });
			    
			    diseaseProceduresFS.addListener(new PanelListenerAdapter(){
			    	public void onExpand(Panel panel) {
			    		//if(pExpand){
			    		gridStoreP.load();
			    		//pExpand = false;
			    	
			    		//panel.expand(true);
			        }
			    	
			    });
			    
			    relatedDiseasesFS.addListener(new PanelListenerAdapter(){
			    	public void onExpand(Panel panel) {
			    		//if(dExpand){
			    		gridStoreD.load();
			    		//dExpand = false;
			    		//}
			    		//panel.expand(true);
			        }
			    	
			    });
		  		

			     
		  
			 
		     GridView view = new GridView();
		     view.setEmptyText("No hay Registros");
		     view.setAutoFill(true);
		     view.setForceFit(true);

		     GridPanel grid = new GridPanel(gridStore, createColModel(false,false));
		     grid.setEnableDragDrop(false);
		     grid.setWidth(500);
		     grid.setHeight(300);
		     grid.setLoadMask(true);  
		     grid.setSelectionModel(new RowSelectionModel());  
		     grid.setFrame(true);  
		     grid.setView(view);
		     grid.setTitle("S&iacute;ntomas Relacionados");
		     
		     GridView view2 = new GridView();
		     view2.setEmptyText("No hay Registros");
		     view2.setAutoFill(true);
		     view2.setForceFit(true);
		     

		     GridPanel grid2 = new GridPanel(gridStoreL, createColModel(false,false));
		     grid2.setEnableDragDrop(false);
		     grid2.setWidth(500);
		     grid2.setHeight(300);
		     grid2.setLoadMask(true);  
		     grid2.setSelectionModel(new RowSelectionModel());  
		     grid2.setFrame(true);  
		     grid2.setView(view2);
		     grid2.setTitle("Ex&aacute;menes Relacionados");
		     
		     GridView view3 = new GridView();
		     view3.setEmptyText("No hay Registros");
		     view3.setAutoFill(true);
		     view3.setForceFit(true);
		     

		     GridPanel grid3 = new GridPanel(gridStoreP, createColModel(false,false));
		     grid3.setEnableDragDrop(false);
		     grid3.setWidth(500);
		     grid3.setHeight(300);
		     grid3.setLoadMask(true);  
		     grid3.setSelectionModel(new RowSelectionModel());  
		     grid3.setFrame(true);  
		     grid3.setView(view3);
		     grid3.setTitle("Procedimientos Relacionados");

		     GridView view4 = new GridView();
		     view4.setEmptyText("No hay Registros");
		     view4.setAutoFill(true);
		     view4.setForceFit(true);

		     GridPanel grid4 = new GridPanel(gridStoreD, createColModel(false,false));
		     grid4.setEnableDragDrop(false);
		     grid4.setWidth(500);
		     grid4.setHeight(300);
		     grid4.setLoadMask(true);  
		     grid4.setSelectionModel(new RowSelectionModel());  
		     grid4.setFrame(true);  
		     grid4.setView(view4);
		     grid4.setTitle("Enfermedades Relacionadas");
			 
		     diseaseSymptomsFS.add(grid, new AnchorLayoutData("100%"));

		     diseaseLabTestsFS.add(grid2, new AnchorLayoutData("100%"));

		     diseaseProceduresFS.add(grid3, new AnchorLayoutData("100%"));

		     relatedDiseasesFS.add(grid4, new AnchorLayoutData("100%"));

			 
			 
			 
			 diseaseForm.add(diseaseSymptomsFS);
			 
			 diseaseForm.add(diseaseLabTestsFS);
			 
			 diseaseForm.add(diseaseProceduresFS);
			 
			 diseaseForm.add(relatedDiseasesFS);
			 
			
			 
			
			 Button cancel = new Button("Cancelar");
			 cancel.addListener(new ButtonListenerAdapter(){
				 public void onClick(Button button, EventObject e){
					 editDiseaseWindow.hide();
				 }

			 });
			 cancel.setIconCls("cancel-icon");
			 proxyPanel.addButton(cancel);  

			 diseaseForm.add(proxyPanel);
			 diseaseForm.doLayout();
			 
		
				 diseaseForm.getForm().load("/semrs/diseaseServlet?diseaseEdit=load&id="+diseaseId, null, Connection.GET, "Cargando...");
				 diseaseForm.getForm().addListener(new FormListenerAdapter(){
			    	   public void onActionComplete(Form form, int httpStatus, String responseText) {
			    		  if(form.findField("diseaseLoadSuccess").getValueAsString().equals("false")){
			    				MessageBox.show(new MessageBoxConfig() {  
			    					{  
			    						setTitle("Error");
			    						setMsg("Esta enfermedad no existe");
			    						setIconCls(MessageBox.ERROR);
			    					    setModal(true);
			    					    setButtons(MessageBox.OK);
			    					    setCallback(new MessageBox.PromptCallback() { 
			    						public void execute(
			    								String btnID,
			    								String text) {
			    							editDiseaseWindow.close();
			    							
			    						}  
			                            });  
			    					}  
			    				});
			    			  
			    		  }    
			    	   }
			    	   public void onActionFailed(Form form, int httpStatus, String responseText) {
			   			MessageBox.show(new MessageBoxConfig() {  
							{  
								setTitle("Error");
								setMsg("Ocurrio un error al tratar de obtener esta enfermedad");
								setIconCls(MessageBox.ERROR);
							    setModal(true);
							    setButtons(MessageBox.OK);
							    setCallback(new MessageBox.PromptCallback() { 
								public void execute(
										String btnID,
										String text) {
									editDiseaseWindow.close();
									
								}  
		                        });  
							}  
						});
			    	    }
			    	   
			       });
			
			 editDiseaseWindow.add(diseaseForm);  
			 editDiseaseWindow.doLayout();
			 return editDiseaseWindow;
		 }
	  
	  
		 public GridPanel getPortalGrid(final Store gridStore,ColumnModel colModel, final TabPanel tabPanel){

			 GridView view = new GridView();
			 view.setEmptyText("No hay Registros");
			 view.setAutoFill(true);
			 view.setForceFit(true);

			 GridPanel grid = new GridPanel(gridStore, colModel);
			 grid.setEnableDragDrop(false);
			 grid.setWidth(530);
			 grid.setHeight(180);
			 grid.setLoadMask(true);  
			 grid.setSelectionModel(new RowSelectionModel());  
			 grid.setFrame(false);  
			 grid.setView(view);
			 
			 //gridStore.load();
             grid.addGridCellListener(new GridCellListener() {  

      			
      			public void onCellDblClick(GridPanel grid, int rowIndex,
      					int colIndex, EventObject e) {
      			    Record r = grid.getStore().getAt(rowIndex);
      			    String id = r.getAsString("encounterId");
      			    ShowcasePanel.adminEncounterScreen.flag1 = false;
      			    ShowcasePanel.adminEncounterScreen.setEncounterId(id);
     		    	ShowcasePanel.adminEncounterScreen.setEdit(false);
     		    	showScreen(tabPanel,ShowcasePanel.adminEncounterScreen, "Editar Consulta","encounter-icon","admEncounterDetail");
      			    
      			}

      			
      			public void onCellClick(GridPanel grid, int rowIndex,
      					int colIndex, EventObject e) {
      		      
      				
      			}


      			public void onCellContextMenu(GridPanel grid, int rowIndex,
      					int cellIndex, EventObject e) {
      				// TODO Auto-generated method stub
      				
      			}
                  });  

			 return grid;
		 }
		 
	  
	 
	 
	 public native void redirect(String url)
	  /*-{
	          $wnd.location.replace(url);

	  }-*/; 
	  
	
	public Record getDeleteRecord() {
		return deleteRecord;
	}

	public void setDeleteRecord(Record deleteRecord) {
		this.deleteRecord = deleteRecord;
	}

	public String getSeverity() {
		return severity;
	}

	public void setSeverity(String severity) {
		this.severity = severity;
	}
	
}
