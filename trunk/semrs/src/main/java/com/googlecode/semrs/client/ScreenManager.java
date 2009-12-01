package com.googlecode.semrs.client;


import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.ArrayList;
import java.util.List;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.googlecode.semrs.client.model.MenuItems;
import com.googlecode.semrs.client.remote.UserService;
import com.googlecode.semrs.client.screens.admin.AddEditUserScreen;
import com.googlecode.semrs.client.screens.admin.AdminRolesScreen;
import com.googlecode.semrs.client.screens.admin.AdminUsersScreen;
import com.googlecode.semrs.client.screens.patient.ListPatientsScreen;
import com.gwtext.client.core.EventCallback;
import com.gwtext.client.core.EventObject;
import com.gwtext.client.core.Function;
import com.gwtext.client.core.UrlParam;
import com.gwtext.client.data.ArrayReader;
import com.gwtext.client.data.FieldDef;
import com.gwtext.client.data.Node;
import com.gwtext.client.data.NodeTraversalCallback;
import com.gwtext.client.data.Record;
import com.gwtext.client.data.RecordDef;
import com.gwtext.client.data.Store;
import com.gwtext.client.data.StringFieldDef;
import com.gwtext.client.util.DelayedTask;
import com.gwtext.client.util.Format;
import com.gwtext.client.widgets.Button;
import com.gwtext.client.widgets.Component;
import com.gwtext.client.widgets.Container;
import com.gwtext.client.widgets.MessageBox;
import com.gwtext.client.widgets.Panel;
import com.gwtext.client.widgets.TabPanel;
import com.gwtext.client.widgets.Toolbar;
import com.gwtext.client.widgets.ToolbarButton;
import com.gwtext.client.widgets.event.ButtonListenerAdapter;
import com.gwtext.client.widgets.event.ContainerListenerAdapter;
import com.gwtext.client.widgets.event.PanelListenerAdapter;
import com.gwtext.client.widgets.event.TabPanelListenerAdapter;
import com.gwtext.client.widgets.form.TextField;
import com.gwtext.client.widgets.form.event.TextFieldListenerAdapter;
import com.gwtext.client.widgets.layout.AccordionLayout;
import com.gwtext.client.widgets.layout.FitLayout;
import com.gwtext.client.widgets.tree.TreeFilter;
import com.gwtext.client.widgets.tree.TreeNode;
import com.gwtext.client.widgets.tree.TreePanel;
import com.gwtext.client.widgets.tree.TreeTraversalCallback;
import com.gwtext.client.widgets.tree.event.TreeNodeListenerAdapter;
import com.gwtextux.client.data.GWTProxy;

public class ScreenManager {

    private Store store;
    private TabPanel appTabPanel;
    private TextField searchField;
    private TreeFilter treeFilter;
    static TreePanel treePanel;
    static Panel accordion;
    private DelayedTask delayedTask = new DelayedTask();
    private boolean isLoading = false;
    private PropertyChangeSupport changes = new PropertyChangeSupport( this );
    
/*
     final AdminUsersScreen adminUsersScreen = new AdminUsersScreen();
     
     final AddEditUserScreen addEditUserScreen = new AddEditUserScreen();
	
     final AdminRolesScreen adminRolesScreen = new AdminRolesScreen();

     final AdminPermScreen adminPermScreen = new AdminPermScreen();
	
     final ListPatientsScreen listPatientsScreen = new ListPatientsScreen();
     */
	
    public ShowcasePanel getScreen(String id){
		
		if(id!=null){
		if(id.equals("AdminUsersScreen")){
			return ShowcasePanel.adminUsersScreen;
		}else if(id.equals("AddEditUserScreen")){
			ShowcasePanel.addEditUserScreen.setUserId("");
			return ShowcasePanel.addEditUserScreen;
		}else if(id.equals("AdminRolesScreen")){
			return ShowcasePanel.adminRolesScreen;
		}else if(id.equals("AdminPermScreen")){
			//return ShowcasePanel.adminPermScreen;
		}else if(id.equals("AdminGroupsScreen")){
			return ShowcasePanel.adminGroupsScreen;
		}else if(id.equals("ListPatientsScreen")){
			return ShowcasePanel.listPatientsScreen;
		}else if(id.equals("AddPatientScreen")){
			return ShowcasePanel.addPatientScreen;
		}else if(id.equals("AdminSymptomsScreen")){
			return ShowcasePanel.adminSymptomsScreen;
		}else if(id.equals("AdminDiseaseScreen")){
			return ShowcasePanel.adminDiseaseScreen;
		}else if(id.equals("AdminTreatmentsScreen")){
			return ShowcasePanel.adminTreatmentsScreen;
		}else if(id.equals("AdminDrugsScreen")){
			return ShowcasePanel.adminDrugsScreen;
		}else if(id.equals("AdminLabTestsScreen")){
			return ShowcasePanel.adminLabTestsScreen;
		}else if(id.equals("AdminProceduresScreen")){
			return ShowcasePanel.adminProceduresScreen;
		}else if(id.equals("AdminComplicationsScreen")){
			return ShowcasePanel.adminComplicationsScreen;
		}else if(id.equals("AdminPatientScreen")){
			ShowcasePanel.adminPatientScreen.setId("admPatientDetail");
			return ShowcasePanel.adminPatientScreen;
		}else if(id.equals("AdminEncounterScreen")){
			ShowcasePanel.adminEncounterScreen.setId("admEncounterDetail");
			return ShowcasePanel.adminEncounterScreen;
		}else if(id.equals("ListEncountersScreen")){
			return ShowcasePanel.listEncountersScreen;
		}
		
		}
		return null;
	}

    public ScreenManager(TabPanel tabPanel) {
        this.appTabPanel = tabPanel;
        this.appTabPanel.addListener(new ContainerListenerAdapter(){
			  public void onRemove(Container self, Component component) {
				  ShowcasePanel.addEditUserScreen.setUserId("");
				 // ShowcasePanel.adminPatientScreen.setPatientId("");
			    }
		});
        this.appTabPanel.addListener(new TabPanelListenerAdapter(){
        	public void onTabChange(TabPanel source, Panel tab) {
        		ShowcasePanel.adminPatientScreen.flag1 = true;
        		ShowcasePanel.adminEncounterScreen.flag1 = true;
        		tab.doLayout();
        		source.doLayout();
            }
      	
      });
        ShowcasePanel.adminUsersScreen.setTabPanel(tabPanel);
        ShowcasePanel.listPatientsScreen.setTabPanel(tabPanel);
        ShowcasePanel.listEncountersScreen.setTabPanel(tabPanel);
        MainTab.setTabPanel(tabPanel);
    }

    
    public void addPropertyChangeListener( PropertyChangeListener l ){
        changes.addPropertyChangeListener( l );
    }
    public void addPropertyChangeListener( String property, PropertyChangeListener l ){
        changes.addPropertyChangeListener( property, l );
    }
    public void removeProertyChangeListener( PropertyChangeListener l ){
        changes.removePropertyChangeListener( l );
    }
    public void removePropertyChangeListener( String property, PropertyChangeListener l ){
        changes.removePropertyChangeListener( property, l);
    }
    
    public void clearPropertyChangeListeners(){
        PropertyChangeListener[] listeners = changes.getPropertyChangeListeners();
        for( int i=0; listeners != null && i < listeners.length; i++ ){
            this.removeProertyChangeListener( listeners[i] );
        }
    }

    public Panel getAccordionNav() {
        accordion = new Panel();
        accordion.setTitle("Acordi&oacute;n");
        accordion.setLayout(new AccordionLayout(true));

        final Store store = getStore();
        
        Record[] records = store.getRecords();
        if(records.length>0){
        for (int i = 0; i < records.length; i++) {
            Record record = records[i];

            String id = record.getAsString("id");
            final String category = record.getAsString("category");
            String title = record.getAsString("title");
            final String iconCls = record.getAsString("iconCls");

            String thumbnail = record.getAsString("thumbnail");
            String qtip = record.getAsString("qtip");
           
     
           // final ShowcasePanel panel = (ShowcasePanel) record.getAsObject("screen");
           
            final ShowcasePanel panel = getScreen(record.getAsString("screen"));
             
            if (category == null) {
                Panel categoryPanel = new Panel();
                categoryPanel.setAutoScroll(true);
                categoryPanel.setLayout(new FitLayout());
                categoryPanel.setId(id + "-acc");
                categoryPanel.setTitle(title);
                categoryPanel.setIconCls(iconCls);
                accordion.add(categoryPanel);
            } else {
                Panel categoryPanel = (Panel) accordion.findByID(category + "-acc");
                TreePanel treePanel = (TreePanel) categoryPanel.findByID(category + "-acc-tree");
                TreeNode root = null;
                if (treePanel == null) {
                    treePanel = new TreePanel();
                    treePanel.setAutoScroll(true);
                    treePanel.setId(category + "-acc-tree");
                    treePanel.setRootVisible(false);
                    root = new TreeNode();
                    treePanel.setRootNode(root);
                    categoryPanel.add(treePanel);
                } else {
                    root = treePanel.getRootNode();
                }

                TreeNode node = new TreeNode();
                node.setText(title);
                node.setId(id);
                if (iconCls != null) node.setIconCls(iconCls);
                if (qtip != null) node.setTooltip(qtip);
                root.appendChild(node);

                addNodeClickListener(node, getScreen(record.getAsString("screen")), iconCls);
            }
         }
        }

        return accordion;
    }

    private void addNodeClickListener(TreeNode node, final Panel panel, final String iconCls) {
        if (panel != null) {
            node.addListener(new TreeNodeListenerAdapter() {
                public void onClick(Node node, EventObject e) {
                    String panelID = panel.getId();
                    if (appTabPanel.hasItem(panelID)) {
                    	//Window.alert("panel exists " + panel.getId() + " node " + node.getId());
                        showScreen(panel, null, null, node.getId());
                    } else {
                    	//Window.alert("panel does not exists " + panel.getId() + " node " + node.getId());
                        TreeNode treeNode = (TreeNode) node;
                        panel.setTitle(treeNode.getText());
                        String nodeIconCls = iconCls;
                        if (iconCls == null) {
                            nodeIconCls = ((TreeNode) treeNode.getParentNode()).getIconCls();
                        }
                        showScreen(panel, treeNode.getText(), nodeIconCls, node.getId());
                    }
                }
            });
        }
    }

    public void showScreen(String historyToken) {
        if (historyToken == null || historyToken.equals("")) {
            appTabPanel.activate(0);
        } else {
            Record record = store.getById(historyToken);
            if (record != null) {
                ShowcasePanel panel = getScreen(record.getAsString("screen"));
                String title = record.getAsString("title");
                String iconCls = record.getAsString("iconCls");
                showScreen(getScreen(record.getAsString("screen")), title, iconCls, historyToken);
            }
        }
    }

    public void showScreen(Panel panel, String title, String iconCls, String screenName) {
        String panelID = panel.getId();
        
        if (appTabPanel.hasItem(panelID)) {
        	//panel.doLayout();
        	//panel.show();
            appTabPanel.scrollToTab(panel, true);
            appTabPanel.activate(panelID);
        } else {
            if (!panel.isRendered()) {
                panel.setTitle(title);
                if (iconCls == null) {
                    iconCls = "plugins-nav-icon";
                }
                panel.setIconCls(iconCls);
            }
            appTabPanel.add(panel);
            appTabPanel.activate(panel.getId());
            
        }
        //panel.show();
       // panel.doLayout();
       // appTabPanel.doLayout();
        History.newItem(screenName);
    }

    public TreePanel getTreeNav() {
    	
        treePanel = new TreePanel();
        treePanel.setTitle("Vista de &Aacute;rbol");
        treePanel.setId("nav-tree");
        treePanel.setWidth(180);
        treePanel.setCollapsible(true);
        treePanel.setAnimate(true);
        treePanel.setEnableDD(false);
        treePanel.setAutoScroll(true);
        treePanel.setContainerScroll(true);
        treePanel.setRootVisible(false);
        treePanel.setBorder(false);
        treePanel.setTopToolbar(getFilterToolbar());

        TreeNode root = new TreeNode("Men&uacute Principal");
        treePanel.setRootNode(root);

		//CreditsPanel creditsPanel = new CreditsPanel();

		//TreeNode creditsNode = new TreeNode("Credits");
		//creditsNode.setIconCls("credits-icon");
		//creditsNode.setId("credits");
		//root.appendChild(creditsNode);
		//addNodeClickListener(creditsNode, creditsPanel, "credits-icon");

		final Store store = getStore();
	
        Record[] records = store.getRecords();
  
        if(records.length>0){
        for (int i = 0; i < records.length; i++) {
            Record record = records[i];

            String id = record.getAsString("id");
            final String category = record.getAsString("category");
            String title = record.getAsString("title");
            final String iconCls = record.getAsString("iconCls");

            String thumbnail = record.getAsString("thumbnail");
            String qtip = record.getAsString("qtip");

            //final ShowcasePanel panel = (ShowcasePanel) record.getAsObject("screen");
            
            final ShowcasePanel panel = getScreen(record.getAsString("screen"));

            TreeNode node = new TreeNode(title);
            node.setId(id);
            if (iconCls != null) node.setIconCls(iconCls);
            if (qtip != null) node.setTooltip(qtip);
            if (category == null || category.equals("")) {
                root.appendChild(node);
            } else {
                Node categoryNode = root.findChildBy(new NodeTraversalCallback() {
                    public boolean execute(Node node) {
                        return node.getId().equals(category);
                    }
                });

                if (categoryNode != null) {
                    categoryNode.appendChild(node);
                }
            }
            addNodeClickListener(node, getScreen(record.getAsString("screen")), iconCls);
        }
        treeFilter = new TreeFilter(treePanel);
        treePanel.expandAll();
        }
        
        return treePanel;
    }

    private void onSearchChange(final boolean filteredOnly) {
        final String filter = searchField.getText();
        if (filter == null || filter.equals("")) {
            treeFilter.clear();
            treeFilter.filterBy(new TreeTraversalCallback() {
                public boolean execute(TreeNode node) {
                    node.setText(Format.stripTags(node.getText()));
                    return true;
                }
            });
        } else {
            treeFilter.filterBy(new TreeTraversalCallback() {
                public boolean execute(TreeNode node) {
                    String text = Format.stripTags(node.getText());
                    node.setText(text);
                    if (text.toLowerCase().indexOf(filter.toLowerCase()) != -1) {
                        node.setText("<b>" + text + "</b>");
                        ((TreeNode) node.getParentNode()).expand();
                        return true;
                    } else {
                        final List childMatches = new ArrayList();
                        node.cascade(new NodeTraversalCallback() {
                            public boolean execute(Node node) {
                                String childText = ((TreeNode) node).getText();
                                if (childText.toLowerCase().indexOf(filter.toLowerCase()) != -1) {
                                    childMatches.add(new Object());
                                }
                                return true;
                            }
                        });
                        return !filteredOnly || childMatches.size() != 0;
                    }
                }
            });
        }
    }

    private Toolbar getFilterToolbar() {
        final Toolbar filterToolbar = new Toolbar();
        ToolbarButton funnelButton = new ToolbarButton();

        funnelButton.setTooltip("El filtro se encuentra DESACTIVADO<br>Haga click para <b>ACTIVAR</b>");
        funnelButton.setCls("x-btn-icon filter-btn");
        funnelButton.setEnableToggle(true);
        funnelButton.addListener(new ButtonListenerAdapter() {
            public void onToggle(Button button, boolean pressed) {
                if (pressed) {
                    DOM.setStyleAttribute(button.getButtonElement(), "backgroundImage", "url(images/funnel_X.gif)");
                    button.setTooltip("El filtro se encuentra ACTIVADO<br>Haga click para <b>DESACTIVAR</b>");
                    onSearchChange(true);
                } else {
                    DOM.setStyleAttribute(button.getButtonElement(), "backgroundImage", "url(images/funnel_plus.gif)");
                    button.setTooltip("El filtro se encuentra DESACTIVADO<br>Haga click para <b>ACTIVAR</b>");
                    treeFilter.clear();
                    onSearchChange(false);
                }
            }
        });
        filterToolbar.addButton(funnelButton);

        searchField = new TextField();
        searchField.setWidth(120);
        searchField.setMaxLength(40);
        searchField.setGrow(false);
        searchField.setSelectOnFocus(true);

        searchField.addListener(new TextFieldListenerAdapter() {
            public void onRender(Component component) {
                searchField.getEl().addListener("keyup", new EventCallback() {
                    public void execute(EventObject e) {
                        delayedTask.delay(500, new Function() {
                            public void execute() {
                                onSearchChange(false);
                            }
                        });
                    }
                });
            }
        });

        filterToolbar.addField(searchField);
        filterToolbar.addFill();

        ToolbarButton expandButton = new ToolbarButton();
        expandButton.setCls("x-btn-icon expand-all-btn");
        expandButton.setTooltip("Expandir Todo");
        expandButton.addListener(new ButtonListenerAdapter() {
            public void onClick(Button button, EventObject e) {
                treePanel.expandAll();
            }
        });
        filterToolbar.addButton(expandButton);

        ToolbarButton collapseButton = new ToolbarButton();
        collapseButton.setCls("x-btn-icon collapse-all-btn");
        collapseButton.setTooltip("Colapsar Todo");
        collapseButton.addListener(new ButtonListenerAdapter() {
            public void onClick(Button button, EventObject e) {
                treePanel.collapseAll();
            }
        });

        filterToolbar.addButton(collapseButton);
        return filterToolbar;
    }
    
    public class GWTProxyImpl extends GWTProxy {
        public void load(int start, int limit, String sort, String dir, final JavaScriptObject o, UrlParam[] baseParams) {
            String[][] params = new String[baseParams.length][];
            for (int i = 0; i < baseParams.length; i++)
                params[i] = new String[] { baseParams[i].getName(), baseParams[i].getValue() };
                UserService.Util.getInstance().getMenuItems(start, limit, sort, dir, params, new AsyncCallback() {
                public void onFailure(Throwable caught) {
                	String errorMessage = caught.toString();
                	Window.alert( "Ocurrio un error al tratar de obtener el menu: " + errorMessage);
                    loadResponse(o, false, 0, (JavaScriptObject) null);
                }

                public void onSuccess(Object result) {
                	MenuItems response = (MenuItems) result;
                    loadResponse(o, true, response.totalRecords, response.data);
                    setStore(store);
                    
                }
            });
        }


    }
    

    public final Store getStore() {
       if (store == null && !isLoading) {
            
            RecordDef recordDef = new RecordDef(new FieldDef[]{
                    new StringFieldDef("id"),
                    new StringFieldDef("category"),
                    new StringFieldDef("title"),
                    new StringFieldDef("iconCls"),
                    new StringFieldDef("thumbnail"),
                    new StringFieldDef("qtip"),
                    new StringFieldDef("screen")
            });
            
            ArrayReader reader = new ArrayReader(0, recordDef);
            store = new Store(new GWTProxyImpl(), reader, false);
            store.setBaseParams(new UrlParam[]{new UrlParam("paramName", "paramValue")});
            store.load(0,10);
            isLoading = true;

      }
        return store;
    }

  
	public void setStore(Store store) {
		this.store = store;
		changes.firePropertyChange( "store", null, store);
	}

	
	
	


}
