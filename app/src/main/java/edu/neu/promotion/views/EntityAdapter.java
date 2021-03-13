package edu.neu.promotion.views;

import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.List;

public class EntityAdapter<EntityType> extends BaseAdapter {

    private List<EntityType> entities;
    private Class<? extends EntityFiller<EntityType>> entityFillerClass;
    private Object tag;

    private View.OnClickListener onClickListener;

    public interface OnItemClickListener<EntityType> {
        void onItemClick(int position, EntityType entity);
    }
    private OnItemClickListener<EntityType> onItemClickListener;

    public EntityAdapter(List<EntityType> entities, Class<? extends EntityFiller<EntityType>> entityFillerClass) {
        this(entities, entityFillerClass, null);
    }

    public EntityAdapter(List<EntityType> entities, Class<? extends EntityFiller<EntityType>> entityFillerClass, Object tag) {
        this.entities = entities;
        this.entityFillerClass = entityFillerClass;
        this.tag = tag;
    }

    @Override
    public int getCount() {
        if (entities == null) {
            return 0;
        }
        return entities.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @SuppressWarnings("unchecked")
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        EntityFiller<EntityType> filler;
        if (convertView == null) {
            filler = getFillerInstance();
            if (filler == null) {
                return null;
            }
            convertView = filler.generateView(parent.getContext());
            convertView.setTag(filler);
        }
        else {
            filler = (EntityFiller<EntityType>) convertView.getTag();
        }
        filler.fill(entities.get(position), tag);
        View clickableView = filler.getClickableView();
        if (clickableView != null) {
            if (onItemClickListener == null) {
                clickableView.setOnClickListener(null);
                clickableView.setTag(null);
            }
            else {
                clickableView.setOnClickListener(onClickListener);
                clickableView.setTag(position);
            }
        }
        return convertView;
    }

    private EntityFiller<EntityType> getFillerInstance() {
        try {
            return entityFillerClass.newInstance();
        } catch (IllegalAccessException | InstantiationException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void setOverrideClick(OnItemClickListener<EntityType> l) {
        onItemClickListener = l;
        if (onItemClickListener != null && onClickListener == null) {
            onClickListener = new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (onItemClickListener != null) {
                        int position = (int) v.getTag();
                        onItemClickListener.onItemClick(position, entities.get(position));
                    }
                }
            };
        }
        notifyDataSetChanged();
    }

    public void notifyDataSetChanged(List<EntityType> newEntities) {
        this.entities = newEntities;
        notifyDataSetChanged();
    }

    public void notifyDataSetChanged(List<EntityType> newEntities, Object newTag) {
        this.entities = newEntities;
        this.tag = newTag;
        notifyDataSetChanged();
    }

    public List<EntityType> getEntities() {
        return entities;
    }
}