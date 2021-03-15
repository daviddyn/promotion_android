package edu.neu.promotion;

import android.widget.Toast;

import com.davidsoft.utils.JsonNode;

import edu.neu.promotion.R;
import edu.neu.promotion.ServerInvoker;
import edu.neu.promotion.StorageManager;
import edu.neu.promotion.components.PageManager;
import edu.neu.promotion.components.RunNetworkTaskPage;

public class TokenRunNetworkTaskPage extends RunNetworkTaskPage {


    private final String token;

    public TokenRunNetworkTaskPage(PageManager pageManager, Object... args) {
        super(pageManager, args);
        token = StorageManager.getValue(getContext(), StorageManager.TOKEN);
    }

    protected final String getToken() {
        return token;
    }
}
