package edu.neu.promotion.views;

import android.content.Context;
import android.view.View;

public interface EntityFiller<EntityType> {

    View generateView(Context context);
    void fill(EntityType entity, Object tag);
    View getClickableView();
}