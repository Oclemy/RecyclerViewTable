package info.camposha.recyclerview_tableview;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.HashSet;

/**
 * A flexible view for providing a limited window into a large data set,like a two-dimensional recyclerView.
 * but it will pin the itemView of first row and first column in their original location.
 */
public class ScrollTableView extends FrameLayout {
    protected RecyclerView recyclerView;
    protected RecyclerView headerRecyclerView;
    protected PanelLineAdapter panelLineAdapter;
    protected TableBaseAdapter tableBaseAdapter;
    protected FrameLayout firstItemView;

    public ScrollTableView(Context context, TableBaseAdapter tableBaseAdapter) {
        super(context);
        this.tableBaseAdapter = tableBaseAdapter;
        initView();
    }

    public ScrollTableView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView();
    }

    public ScrollTableView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();
    }

    private void initView() {
        LayoutInflater.from(getContext()).inflate(R.layout.view_scrollable_panel, this, true);
        recyclerView = (RecyclerView) findViewById(R.id.recycler_content_list);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        firstItemView = (FrameLayout) findViewById(R.id.first_item);
        headerRecyclerView = (RecyclerView) findViewById(R.id.recycler_header_list);
        headerRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        headerRecyclerView.setHasFixedSize(true);
        if (tableBaseAdapter != null) {
            panelLineAdapter = new PanelLineAdapter(tableBaseAdapter, recyclerView, headerRecyclerView);
            recyclerView.setAdapter(panelLineAdapter);
            setUpFirstItemView(tableBaseAdapter);
        }
    }

    private void setUpFirstItemView(TableBaseAdapter tableBaseAdapter) {
        RecyclerView.ViewHolder viewHolder = tableBaseAdapter.onCreateViewHolder(firstItemView, tableBaseAdapter.getItemViewType(0, 0));
        tableBaseAdapter.onBindViewHolder(viewHolder, 0, 0);
        firstItemView.addView(viewHolder.itemView);
    }

    public void notifyDataSetChanged() {
        if (panelLineAdapter != null) {
            setUpFirstItemView(tableBaseAdapter);
            panelLineAdapter.notifyDataChanged();
        }
    }

    /**
     * @param tableBaseAdapter {@link TableBaseAdapter}
     */
    public void setTableBaseAdapter(TableBaseAdapter tableBaseAdapter) {
        if (this.panelLineAdapter != null) {
            panelLineAdapter.setTableBaseAdapter(tableBaseAdapter);
            panelLineAdapter.notifyDataSetChanged();
        } else {
            panelLineAdapter = new PanelLineAdapter(tableBaseAdapter, recyclerView, headerRecyclerView);
            recyclerView.setAdapter(panelLineAdapter);
        }
        this.tableBaseAdapter = tableBaseAdapter;
        setUpFirstItemView(tableBaseAdapter);
    }

    /**
     * Adapter used to bind dataSet to cell View that are displayed within every row of {@link ScrollTableView}.
     */
    private static class PanelLineItemAdapter extends RecyclerView.Adapter {

        private TableBaseAdapter tableBaseAdapter;
        private int row;

        public PanelLineItemAdapter(int row, TableBaseAdapter tableBaseAdapter) {
            this.row = row;
            this.tableBaseAdapter = tableBaseAdapter;
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return this.tableBaseAdapter.onCreateViewHolder(parent, viewType);
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            this.tableBaseAdapter.onBindViewHolder(holder, row, position + 1);
        }

        @Override
        public int getItemViewType(int position) {
            return this.tableBaseAdapter.getItemViewType(row, position + 1);
        }

        @Override
        public int getItemCount() {
            return tableBaseAdapter.getColumnCount() - 1;
        }

        public void setRow(int row) {
            this.row = row;
        }
    }

    /**
     * Adapter used to bind dataSet to views that are displayed within a{@link ScrollTableView}.
     */
    private static class PanelLineAdapter extends RecyclerView.Adapter<PanelLineAdapter.ViewHolder> {
        private TableBaseAdapter tableBaseAdapter;
        private RecyclerView headerRecyclerView;
        private RecyclerView contentRV;
        private HashSet<RecyclerView> observerList = new HashSet<>();
        private int firstPos = -1;
        private int firstOffset = -1;

        public PanelLineAdapter(TableBaseAdapter tableBaseAdapter, RecyclerView contentRV, RecyclerView headerRecyclerView) {
            this.tableBaseAdapter = tableBaseAdapter;
            this.headerRecyclerView = headerRecyclerView;
            this.contentRV = contentRV;
            initRecyclerView(headerRecyclerView);
            setUpHeaderRecyclerView();

        }

        public void setTableBaseAdapter(TableBaseAdapter tableBaseAdapter) {
            this.tableBaseAdapter = tableBaseAdapter;
            setUpHeaderRecyclerView();
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public int getItemCount() {
            return tableBaseAdapter.getRowCount() - 1;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            ViewHolder viewHolder = new ViewHolder(LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.listitem_content_row, parent, false));
            initRecyclerView(viewHolder.recyclerView);
            return viewHolder;
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            PanelLineItemAdapter rowItemAdapter = (PanelLineItemAdapter) holder.recyclerView.getAdapter();
            if (rowItemAdapter == null) {
                rowItemAdapter = new PanelLineItemAdapter(position + 1, tableBaseAdapter);
                holder.recyclerView.setAdapter(rowItemAdapter);
            } else {
                rowItemAdapter.setRow(position + 1);
                rowItemAdapter.notifyDataSetChanged();
            }
            if (holder.firstColumnItemVH == null) {
                RecyclerView.ViewHolder viewHolder = tableBaseAdapter.onCreateViewHolder(holder.firstColumnItemView, tableBaseAdapter.getItemViewType(position + 1, 0));
                holder.firstColumnItemVH = viewHolder;
                tableBaseAdapter.onBindViewHolder(holder.firstColumnItemVH, position + 1, 0);
                holder.firstColumnItemView.addView(viewHolder.itemView);
            } else {
                tableBaseAdapter.onBindViewHolder(holder.firstColumnItemVH, position + 1, 0);
            }

        }


        public void notifyDataChanged() {
            setUpHeaderRecyclerView();
            notifyDataSetChanged();
        }


        private void setUpHeaderRecyclerView() {
            if (tableBaseAdapter != null) {
                if (headerRecyclerView.getAdapter() == null) {
                    PanelLineItemAdapter lineItemAdapter = new PanelLineItemAdapter(0, tableBaseAdapter);
                    headerRecyclerView.setAdapter(lineItemAdapter);
                } else {
                    headerRecyclerView.getAdapter().notifyDataSetChanged();
                }
            }
        }

        public void initRecyclerView(RecyclerView recyclerView) {
            recyclerView.setHasFixedSize(true);
            LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
            if (layoutManager != null && firstPos > 0 && firstOffset > 0) {
                layoutManager.scrollToPositionWithOffset(PanelLineAdapter.this.firstPos + 1, PanelLineAdapter.this.firstOffset);
            }
            observerList.add(recyclerView);
            recyclerView.setOnTouchListener(new OnTouchListener() {
                @Override
                public boolean onTouch(View view, MotionEvent motionEvent) {
                    switch (motionEvent.getAction()) {
                        case MotionEvent.ACTION_DOWN:
                        case MotionEvent.ACTION_POINTER_DOWN:
                            for (RecyclerView rv : observerList) {
                                rv.stopScroll();
                            }
                    }
                    return false;
                }
            });
            recyclerView.setOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                    super.onScrolled(recyclerView, dx, dy);
                    LinearLayoutManager linearLayoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
                    int firstPos = linearLayoutManager.findFirstVisibleItemPosition();
                    View firstVisibleItem = linearLayoutManager.getChildAt(0);
                    if (firstVisibleItem != null) {
                        int firstRight = linearLayoutManager.getDecoratedRight(firstVisibleItem);
                        for (RecyclerView rv : observerList) {
                            if (recyclerView != rv) {
                                LinearLayoutManager layoutManager = (LinearLayoutManager) rv.getLayoutManager();
                                if (layoutManager != null) {
                                    PanelLineAdapter.this.firstPos = firstPos;
                                    PanelLineAdapter.this.firstOffset = firstRight;
                                    layoutManager.scrollToPositionWithOffset(firstPos + 1, firstRight);
                                }
                            }
                        }
                    }
                }

                @Override
                public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                    super.onScrollStateChanged(recyclerView, newState);
                }
            });
        }

        private HashSet<RecyclerView> getRecyclerViews() {
            HashSet<RecyclerView> recyclerViewHashSet = new HashSet<>();
            recyclerViewHashSet.add(headerRecyclerView);

            for (int i = 0; i < contentRV.getChildCount(); i++) {
                recyclerViewHashSet.add((RecyclerView) contentRV.getChildAt(i).findViewById(R.id.recycler_line_list));
            }
            return recyclerViewHashSet;
        }


        static class ViewHolder extends RecyclerView.ViewHolder {
            public RecyclerView recyclerView;
            public FrameLayout firstColumnItemView;
            public RecyclerView.ViewHolder firstColumnItemVH;

            public ViewHolder(View view) {
                super(view);
                this.recyclerView = (RecyclerView) view.findViewById(R.id.recycler_line_list);
                this.firstColumnItemView = (FrameLayout) view.findViewById(R.id.first_column_item);
                this.recyclerView.setLayoutManager(new LinearLayoutManager(view.getContext(), LinearLayoutManager.HORIZONTAL, false));
            }
        }

    }


}
