package myfirstapp;


import myfirstapp.sys.App;

import org.eclipse.ui.IFolderLayout;
import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IPerspectiveFactory;

public class Perspective implements IPerspectiveFactory {

	public void createInitialLayout(IPageLayout layout) {
		layout.setEditorAreaVisible(false);
		//◊Û≤‡≤Àµ• ”Õº
		IFolderLayout LeftFolder = layout.createFolder("LeftFolder",
				IPageLayout.LEFT, 0.2f, layout.getEditorArea());
		LeftFolder.addView(App.App_view_menuList_ID);
		layout.getViewLayout(App.App_view_menuList_ID).setCloseable(false);//∆¡±Œ”“…œΩ«πÿ±’∞¥≈•
		// ”“…œ ”Õº
		IFolderLayout rightUpFolder = layout.createFolder("rightUpFolder",
				IPageLayout.TOP, 0.8f, layout.getEditorArea());
		rightUpFolder.addPlaceholder(App.App_view_creatIndex_ID);
		//rightUpFolder.addView(App.App_view_creatIndex_ID);
		rightUpFolder.addView(App.App_view_singleFileCompareView_ID);
		rightUpFolder.addView(App.App_view_multiFileCompareView_ID);
		
		//layout.getViewLayout(App.App_view_creatIndex_ID).setCloseable(false);//∆¡±Œ”“…œΩ«πÿ±’∞¥≈•
		layout.getViewLayout(App.App_view_singleFileCompareView_ID).setCloseable(false);//∆¡±Œ”“…œΩ«πÿ±’∞¥≈•
		layout.getViewLayout(App.App_view_multiFileCompareView_ID).setCloseable(false);//∆¡±Œ”“…œΩ«πÿ±’∞¥≈•

		// ”“œ¬ ”Õº
		IFolderLayout rightBottomFolder = layout.createFolder("LeftBottom",
				IPageLayout.BOTTOM, 0.8f, "rightUpFolder");
		//rightBottomFolder.addView(App.App_view_detail_ID);
		//layout.getViewLayout(App.App_view_detail_ID).setCloseable(false);//∆¡±Œ”“…œΩ«πÿ±’∞¥≈•
	}
}
