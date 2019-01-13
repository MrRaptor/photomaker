package com.sevencats.photomaker;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.constraint.motion.MotionLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Interpolator;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.ToggleButton;

import com.sevencats.photomaker.Adapter.ViewPagerAdapter;
import com.sevencats.photomaker.Interface.FiltersListFragmentListener;
import com.sevencats.photomaker.Interface.OnStickerSelectedListener;
import com.sevencats.photomaker.Interface.OverlayListFragmentListener;

import com.sevencats.photomaker.View.ImageEntity;
import com.sevencats.photomaker.View.Layer;
import com.sevencats.photomaker.View.MotionView;
import com.sevencats.photomaker.utils.BitmapUtils;
import com.zomato.photofilters.imageprocessors.Filter;

import java.io.IOException;

import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;


public class Home extends AppCompatActivity implements FiltersListFragmentListener, OverlayListFragmentListener, OnStickerSelectedListener {

    protected static MotionView motionView;
    private LayoutInflater layoutInflater;
    private ImageView photo, btnBack, btnSave, btnClean, btnStickers;
    private Bitmap originalBitmap, editBitmap, finalBitmap;
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private FiltersListFragment filtersListFragment;
    private OverlayListFragment overlayListFragment;
    private CoordinatorLayout coordinatorLayout;
    private RelativeLayout toolBarContainer;
    private int alphaFinal = 0;
    private Toolbar toolbar;
    static public String path;
    private ToggleButton showSpinnerMenu;
    private FrameLayout spinnerMenuFrame;
    ProgressBar progressBar;
    MotionLayout spinnerMenu;
    final Handler handler = new Handler();

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                .setFontAttrId(R.attr.fontPath)
                .build()
        );

        Intent intent = getIntent();
        LoadImage loadImage = new LoadImage();
        path = intent.getStringExtra("path");
        loadImage.execute(path);

        photo = findViewById(R.id.image_preview);
        viewPager = findViewById(R.id.viewpager);
        tabLayout = findViewById(R.id.tabs);
        coordinatorLayout = findViewById(R.id.coordinator);
        motionView = findViewById(R.id.motion_view);
        toolbar = findViewById(R.id.toolBar);
        toolBarContainer = findViewById(R.id.toolBarContainer);
        spinnerMenu = findViewById(R.id.motionLayout);
        showSpinnerMenu = findViewById(R.id.show_spinner_menu);
        spinnerMenuFrame = findViewById(R.id.spinner_menu_frame);
        btnStickers = findViewById(R.id.btn_stickers);
        layoutInflater = LayoutInflater.from(Home.this);

        spinnerMenuFrame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showSpinnerMenu.performClick();
            }
        });

        showSpinnerMenu.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    spinnerMenu.setVisibility(View.VISIBLE);
                    spinnerMenu.transitionToEnd();
                    spinnerMenu.setTransitionListener(new MotionLayout.TransitionListener() {
                        @Override
                        public void onTransitionChange(MotionLayout motionLayout, int i, int i1, float v) {

                        }

                        @Override
                        public void onTransitionCompleted(MotionLayout motionLayout, int i) {
                            btnStickers.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    StickersFragment stickersFragment = StickersFragment.getInstance();
                                    stickersFragment.setListener(Home.this);
                                    stickersFragment.show(getSupportFragmentManager(), stickersFragment.getTag());
                                }
                            });
                        }
                    });
                } else {
                    spinnerMenu.transitionToStart();
                }
            }
        });

        setSupportActionBar(toolbar);
        View view = layoutInflater.inflate(R.layout.top_toolbar, toolBarContainer);
        setupToolbarButtons(view);
    }

    private void setupViewPager(final ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());

        filtersListFragment = new FiltersListFragment();
        filtersListFragment.setListener(this);

        overlayListFragment = new OverlayListFragment();
        overlayListFragment.setListener(this);

        adapter.addFragment(filtersListFragment, "FILTERS");
        adapter.addFragment(overlayListFragment, "OVERLAY");
        viewPager.setAdapter(adapter);

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());

                if (tab.getPosition() == 1) {
                    overlayListFragment.removeMenu();
                }
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
    }

    public void onFilterSelected(final Filter filter) {
        photo.post(new Runnable() {
            @Override
            public void run() {
                setupAcceptToolbar();
                editBitmap = finalBitmap.copy(Bitmap.Config.ARGB_8888, true);
                photo.setImageBitmap(filter.processFilter(editBitmap));
                photo.invalidate();
            }
        });
    }

    @Override
    public void onOverlaySelected(final int resID) {
        photo.post(new Runnable() {
            @Override
            public void run() {
                setupAcceptToolbar();
                editBitmap = finalBitmap.copy(Bitmap.Config.ARGB_8888, true);
                editBitmap = BitmapUtils.overlay(Home.this, editBitmap, resID, 255, false, true);
                photo.setImageBitmap(editBitmap);
                photo.postInvalidate();
                BitmapUtils.flipRotate = 0;
            }
        });
    }

    @Override
    public void onAlphaChange(final int alpha, final int resID) {
        photo.post(new Runnable() {
            @Override
            public void run() {
                alphaFinal = alpha;
                boolean isFlip = false;
                if (BitmapUtils.flipRotate == 180) isFlip = true;
                editBitmap = finalBitmap.copy(Bitmap.Config.ARGB_8888, true);
                editBitmap = BitmapUtils.overlay(Home.this, editBitmap, resID, alpha, isFlip, true);
                photo.setImageBitmap(editBitmap);
                photo.postInvalidate();
            }
        });
    }

    @Override
    public void onMirrorSelected(final int resID) {
        photo.post(new Runnable() {
            @Override
            public void run() {
                editBitmap = finalBitmap.copy(Bitmap.Config.ARGB_8888, true);
                editBitmap = BitmapUtils.overlay(Home.this, editBitmap, resID, 255, true, false);
                photo.setImageBitmap(editBitmap);
                photo.postInvalidate();
            }
        });
    }

    private void openImage(String path) {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_VIEW);
        intent.setDataAndType(Uri.parse(path), "image/*");
        startActivity(intent);
    }

    //TOOLBARS
    public void setupAcceptToolbar() {
        toolBarContainer.removeAllViews();
        View acceptToolbar = layoutInflater.inflate(R.layout.top_accept_toolbar, toolBarContainer);
        ImageView btnCancel = acceptToolbar.findViewById(R.id.btnCancel);
        ImageView btnOK = acceptToolbar.findViewById(R.id.btnOK);
        final ImageView btnCleanSticker = acceptToolbar.findViewById(R.id.btnCleanSticker);

        if (motionView.getEntities().size() > 0) {
            btnCleanSticker.setVisibility(View.VISIBLE);
            btnCleanSticker.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    handler.postDelayed(new Runnable() {
                        public void run() {
                            motionView.deletedSelectedEntity();
                            if (motionView.getEntities().size() < 1) {
                                btnCleanSticker.setVisibility(View.INVISIBLE);
                            }
                        }
                    }, 300);
                }
            });
        }

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        toolBarContainer.removeAllViews();
                        View view = layoutInflater.inflate(R.layout.top_toolbar, toolBarContainer);
                        editBitmap = null;
                        motionView.clearEntity();
                        motionView.unselectEntity();
                        motionView.updateUI();
                        overlayListFragment.removeMenu();
                        photo.setImageBitmap(finalBitmap);
                        setupToolbarButtons(view);
                    }
                }, 300);
            }
        });

        btnOK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        toolBarContainer.removeAllViews();
                        View view = layoutInflater.inflate(R.layout.top_toolbar, toolBarContainer);
                        setupToolbarButtons(view);
                        if (motionView.getEntities().size() > 0) {
                            Bitmap motionBitmap = Bitmap.createBitmap(motionView.getWidth(), motionView.getHeight(), finalBitmap.getConfig());
                            motionBitmap = motionView.getThumbnailImage(motionBitmap);
                            motionBitmap = Bitmap.createScaledBitmap(motionBitmap, finalBitmap.getWidth(), finalBitmap.getHeight(), true);
                            finalBitmap = BitmapUtils.overlayTwoBitmaps(editBitmap, motionBitmap, null);
                            photo.setImageBitmap(finalBitmap);
                            motionView.clearEntity();
                        } else {
                            finalBitmap = editBitmap;
                        }
                    }
                }, 300);
            }
        });
    }

    public void setupToolbarButtons(View view) {
        btnBack = view.findViewById(R.id.btnBack);
        btnSave = view.findViewById(R.id.btnSave);
        btnClean = view.findViewById(R.id.btnClean);

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Home.this, Launcher.class);
                startActivity(intent);
                finish();
            }
        });

        btnClean.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(Home.this);
                builder.setMessage("Are you sure ?").
                        setPositiveButton("Yea", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                finalBitmap = originalBitmap.copy(Bitmap.Config.ARGB_8888, true);
                                photo.setImageBitmap(finalBitmap);
                            }
                        }).
                        setNegativeButton("Nope", null);
                AlertDialog dialog = builder.create();
                dialog.setOnShowListener(new DialogInterface.OnShowListener() {
                    @Override
                    public void onShow(final DialogInterface dialog) {
                        ((AlertDialog) dialog).getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.parseColor("#D522EC"));
                        Button negative = ((AlertDialog) dialog).getButton(AlertDialog.BUTTON_NEGATIVE);
                        negative.setBackgroundResource(R.drawable.ripple_btn);
                        negative.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                handler.postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        dialog.dismiss();
                                    }
                                },300);
                            }
                        });
                    }
                });
                dialog.show();
            }
        });

        btnSave.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("ResourceAsColor")
            @Override
            public void onClick(View v) {
                try {
                    final String path = BitmapUtils.insertImage(getContentResolver(), finalBitmap, System.currentTimeMillis() + "profile.jpg", null);

                    if (!TextUtils.isEmpty(path)) {
                        Snackbar snackbar = Snackbar.make(coordinatorLayout, "Image saved to gallery!", Snackbar.LENGTH_LONG).setAction(
                                "OPEN", new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        openImage(path);
                                    }
                                }
                        ).setActionTextColor(Color.WHITE);
                        snackbar.show();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        });
    }

    //Sticker
    public void addSticker(final int stickerResId) {
        Layer layer = new Layer();
        Bitmap pica = BitmapFactory.decodeResource(getResources(), stickerResId);
        ImageEntity entity = new ImageEntity(layer, pica, motionView.getWidth(), motionView.getHeight());
        motionView.addEntityAndPosition(entity);
    }

    @Override
    public void onStickerSelected(int resID) {
        addSticker(resID);
        setupAcceptToolbar();
    }

    @Override
    public void hideStickerFragment(boolean isTrue) {
        if(isTrue) showSpinnerMenu.performClick();
    }


    // Loading image on background class
    public class LoadImage extends AsyncTask<String, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressBar = findViewById(R.id.progressBar);
            progressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected Void doInBackground(String... strings) {
            originalBitmap = BitmapUtils.decodeBitmap(640, 640, strings[0]);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            finalBitmap = originalBitmap.copy(Bitmap.Config.ARGB_8888, true);
            editBitmap = finalBitmap.copy(Bitmap.Config.ARGB_8888, true);
            photo.setImageBitmap(finalBitmap);
            setupViewPager(viewPager);
            tabLayout.setupWithViewPager(viewPager);
            progressBar.setVisibility(View.INVISIBLE);
        }
    }

    //Reverse animation  class
    public class ReverseInterpolator implements Interpolator {
        @Override
        public float getInterpolation(float paramFloat) {
            return Math.abs(paramFloat - 1f);
        }
    }
}
