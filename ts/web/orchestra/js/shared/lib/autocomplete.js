/**
 * Autocompletion plugin.
 *
 * Requires :
 * - jquery
 * - jquery.ui
 * - lib/common.js
 * - lib/codification.js
 */
(function($) {

  var tsautocomplete = {


    /*---------------------------------------------------------------------------*/
    /*             Options                                                       */
    /*---------------------------------------------------------------------------*/


    options: {

      // Standard jQuery UI autocomplete options.
      autocomplete: {
        minLength: 3,
        focus: function(event, ui) {
          $(this).val(ui.item.displayLabel());
          return false;
        },
        select: function(event, ui) {
          $(this).val(ui.item.displayLabel());
          return false;
        }
      },

      // selector of target <select>
      target: null,

      // false, "destination" or "company"
      type: false

    },


    /*---------------------------------------------------------------------------*/
    /*          Private methods                                                  */
    /*---------------------------------------------------------------------------*/


    // Initializes the CMS.
    _create: function() {
      var $this = this;

      $this._patchAutoCompletion();

      $this.element.autocomplete($this.options.autocomplete);

      if ($this.options.type === "destination") {
        $this._patchDestinationAutoCompletion($this.element);
      } else if ($this.options.type === "company") {
        $this._patchCompanyAutoCompletion($this.element);
      }

    },


    /**
     * Adds a <strong></strong> container around the search term in the autocompletion search results.
     *
     * This is a (temporary) bad hack because there is a regexp obj created for every item rendered in the list,
     * which is very inefficient : the regexp obj should to be re-used for all items.
     */
    _patchAutoCompletion: function() {
      $.ui.autocomplete.prototype._renderItem = function(ul, item) {
        if (item.autocompleteLabel != null) {
          var label = item.autocompleteLabel().replace(new RegExp("(?![^&;]+;)(?!<[^<>]*)(" + $.ui.autocomplete.escapeRegex(this.term) + ")(?![^<>]*>)(?![^&;]+;)", "gi"), "<strong>$1</strong>");
          return $("<li></li>").data("item.autocomplete", item).append($("<a>" + label + "</a>").addClass(item.displayClass())).appendTo(ul);
        } else {
          return $("<li></li>").data("item.autocomplete", item).append($("<a></a>").text(item.label + " (" + item.code + ")")).appendTo(ul);
        }
      };
    },

    /**
     * Adds a group function to the destinations autocompletion.
     */
    _patchDestinationAutoCompletion: function(input) {
      return $(input).each(function() {
        $(this).data("autocomplete")._renderMenu = function(ul, destinations) {
          var self = this;
          var codificationLabel = "";
          destinations.sort(Destination.prototype.compare);
          $.each(destinations, function(i, d) {
            var destination = new Destination(d);
            if (destination.codificationLabel() != codificationLabel) {
              codificationLabel = destination.codificationLabel();
              ul.append("<li class=\"ui-autocomplete-category\">" + codificationLabel + "</li>");
            }
            self._renderItem(ul, destination);
          });
        };
      });
    },

    /**
     * Adds a group function to the travel companies autocompletion.
     */
    _patchCompanyAutoCompletion: function(input) {
      return $(input).each(function() {
        $(this).data("autocomplete")._renderMenu = function(ul, companies) {
          var self = this;
          var codificationLabel = "";
          companies.sort(Company.prototype.compare);
          $.each(companies, function(i, c) {
            var company = new Company(c);
            if (company.codificationLabel() != codificationLabel) {
              codificationLabel = company.codificationLabel();
              ul.append("<li class=\"ui-autocomplete-category\">" + codificationLabel + "</li>");
            }
            self._renderItem(ul, company);
          });
        };
      });
    },

    /*---------------------------------------------------------------------------*/
    /*          Public methods                                                   */
    /*---------------------------------------------------------------------------*/


    // Destroys the plugin.
    destroy: function() {
      // Default destroy
      $.Widget.prototype.destroy.apply(this, arguments);
      // Specific destroy
    },

    /**
     * Adds an autocompletion item to the target elements.
     */
    add: function(ac) {
      var $this = this;
      if ($this.options.target != null) {

        // Get the code and label
        var code  = ac.displayCode();
        var label = ac.displayLabel();

        return $($this.options.target).each(function() {

          // Append item if it hasn't been modified and doesn't exist yet
          var modified = $this.element.val().length != label.length;
          var exists = $(this).children("option").filter(function() {return this.value.toLowerCase() == code.toLowerCase();}).length > 0;
          if (!modified && !exists) {
            $(this).append("<option value=\"" + code + "\">" + label + "</option>");
          }

        });

      }
    },

    /**
     * Removes selected autocompletion items from the target elements.
     */
    remove: function() {
      var $this = this;
      if ($this.options.target != null) {
        return $($this.options.target).each(function() {

          // Remove the selected options
          $(this).children("option:selected").remove();

        });
      }
    }


  };


  $.widget("ui.tsautocomplete", tsautocomplete);


})(jQuery);
