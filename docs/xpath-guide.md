# XPath Guide for Stable Selectors

This guide focuses on stable selectors found in Slim templates in the `homey` app. Prefer attributes that describe behavior (`data-controller`, `data-action`) or Turbo Frames (`turbo-frame`) instead of styling-only classes.

## Why avoid Tailwind classes
Tailwind utility classes are optimized for styling and often change during UI refactors, responsive tweaks, or design system updates. Using them as selectors makes tests brittle, because a purely visual change can break automation. Data attributes and Turbo Frame IDs are tied to behavior and are more stable over time.

## Examples from Slim templates

Each snippet below uses stable data attributes from real templates in `homey`, with the source file path for reference.

1. **Sortable tags list (data-controller)**
   - File: `homey/app/views/templates/tags/_tags.slim`
   - XPath:
     ```xpath
     //ul[@data-controller='sortable']
     ```

2. **Template editor preview (data-controller)**
   - File: `homey/app/views/templates/notifications/_form.html.slim`
   - XPath:
     ```xpath
     //div[@data-controller='organisms--template-editor-preview']
     ```

3. **Task footer with outlet (data-controller + data-*)**
   - File: `homey/app/views/shared/forms/_task_layout.slim`
   - XPath:
     ```xpath
     //div[@data-controller='organisms--task-footer'
           and @data-organisms--task-footer-organisms--task-form-outlet='.task__form__container']
     ```

4. **Dropzone click action (data-action)**
   - File: `homey/app/views/referrals/documents/new.html.slim`
   - XPath:
     ```xpath
     //div[@data-action='click->v2--molecules--dropzone#triggerFileInput']
     ```

5. **Client search events (data-action)**
   - File: `homey/app/views/leads/clients/_client_fields.slim`
   - XPath:
     ```xpath
     //div[@data-controller='leads--client-search'
           and @data-action='phoneinput:connected->leads--client-search#setIti leads--client-lookup:clientSelected@window->leads--client-search#clientSelected']
     ```

6. **Remote filter Turbo Frame (turbo-frame)**
   - File: `homey/app/components/organisms/dashboard_filters/remote_filter_component.slim`
   - XPath:
     ```xpath
     //turbo-frame[@loading='lazy' and @data-page-spinner='false']
     ```

7. **Playlist container (id + data-controller)**
   - File: `homey/app/views/referrals/playlists/_playlist_items.html.slim`
   - XPath:
     ```xpath
     //div[@id='playlist' and @data-controller='organisms--playlist']
     ```

8. **Signature modal controls (turbo-frame + data-action)**
   - File: `homey/app/views/signatures/new.html.slim`
   - XPath:
     ```xpath
     //turbo-frame[@id='modal']//button[@data-action='click->signature-modal#closeModal']
     ```
