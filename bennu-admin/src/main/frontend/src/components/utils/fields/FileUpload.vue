<template>
  <div
    :class="{ 'f-field--danger': invalid, 'f-field--required': required, 'f-field--disabled': disabled }"
    class="f-field f-field--file"
  >
    <fieldset>
      <legend
        v-if="label"
        class="f-field__label"
      >
        {{ label }}
      </legend>
      <p
        v-if="description"
        :id="`file-upload-${name}-description`"
        class="f-field__description"
      >
        {{ description }}
      </p>
      <div
        class="f-field__input"
        @dragover="isDraggingFile = true"
        @dragleave="isDraggingFile = false"
        @drop="isDraggingFile = false"
      >
        <input
          :id="name"
          :key="inputKey"
          ref="input"
          :name="name"
          type="file"
          class="f-field__file-input"
          :class="{ 'f-field__file-input--filled': hasFile }"
          :accept="accept"
          :aria-required="String(required)"
          :aria-describedby="[description ? `file-upload-${name}-description` : undefined].concat($attrs['aria-describedby']).join(' ')"
          :aria-invalid="String(invalid)"
          :aria-errormessage="invalid ? `file-upload-${name}-error` : undefined"
          :aria-disabled="String(disabled)"
          :disabled="hasFile || disabled"
          v-bind="$attrs"
          @change="receiveFile"
        >
        <label
          v-if="!hasFile"
          class="f-field__trigger"
          :for="name"
          :class="{ 'f-field__trigger--dragging': isDraggingFile }"
        >
          <!-- FIXME: this will display mimetypes, not only file extensions -->
          <span class="f-field__trigger-text">
            {{ placeholder || $tc('trigger-label.not-filled', (fileTypes && fileTypes.length) || 0, { format: fileTypes && fileTypes.join(', ') }) }}
          </span>
        </label>
        <label
          v-else
          :for="name"
          class="f-field__trigger"
        >
          <span class="sr-only">
            {{ $t('trigger-label.filled.aria', { file: value.name }) }}
          </span>
          <span aria-hidden="true">
            {{ value.name }}
          </span>
          <button
            v-if="!disabled"
            class="f-field__btn-clear"
            @click.prevent="removeFile"
          >
            <span class="sr-only">
              {{ $t('button-remove.aria', { file: value.name }) }}
            </span>
            <svg
              aria-hidden="true"
              width="16"
              height="16"
              xmlns="http://www.w3.org/2000/svg"
            >
              <g
                fill="none"
                fill-rule="evenodd"
              >
                <path
                  class="svg-path"
                  d="M8 0c4.41828 0 8 3.58172 8 8s-3.58172 8-8 8-8-3.58172-8-8 3.58172-8 8-8zm3 7H5l-.12544.0078C4.3815 7.0695 4 7.4902 4 8c0 .55228.44772 1 1 1h6l.12544-.0078C11.6185 8.9305 12 8.5098 12 8c0-.55228-.44772-1-1-1z"
                />
              </g>
            </svg>
          </button>
        </label>
      </div>
      <div
        v-if="$slots && $slots.tooltip"
        class="f-field__tooltip"
      >
        <tooltip>
          <slot
            slot="message"
            name="tooltip"
          />
          <template #trigger="{ attrs, events }">
            <slot
              name="tooltip-trigger"
              :attrs="attrs"
              :events="events"
            >
              <button
                v-bind="attrs"
                :aria-label="$t('tooltip-trigger.aria-label')"
                v-on="events"
              >
                i
              </button>
            </slot>
          </template>
        </tooltip>
      </div>
      <transition name="validation-fade">
        <div
          v-if="invalid"
          :id="`file-upload-${name}-error`"
          class="f-field__validation"
          role="alert"
        >
          <slot name="error-message" />
        </div>
      </transition>
      <!-- FIXME: this was needed to display a link to a file template. rethink this and make it neater -->
      <div
        v-if="!!$slots['additional-info']"
        class="f-field__additional-info"
      >
        <slot name="additional-info" />
      </div>
    </fieldset>
  </div>
</template>

<script>
export default {
  name: 'FileUpload',
  components: {
    Tooltip: () => import('@/components/utils/Tooltip.vue')
  },
  i18n: {
    messages: {
      pt: {
        'trigger-label': {
          'not-filled': 'Carregar ficheiro | Carregar ficheiro no formato {format} | Carregar ficheiro nos formatos {format}',
          filled: {
            aria: 'Carregou o ficheiro “{file}”. Pode utilizar o botão a seguir para remover o ficheiro escolhido, e seleccionar um novo.'
          }
        },
        'button-remove.aria': 'Remover ficheiro “{file}”',
        'tooltip-trigger': {
          'aria-label': 'Mostrar dica de preenchimento'
        }
      },
      en: {
        'trigger-label': {
          'not-filled': 'Upload file | Upload file in the format: {format} | Upload file in one of the formats: {format}',
          filled: {
            aria: 'You uploaded the file “{file}”. You may use the button after this input to remove the chosen file and select a new one.'
          }
        },
        'button-remove.aria': 'Remove file “{file}”',
        'tooltip-trigger': {
          'aria-label': 'Show filling hint'
        }
      }
    }
  },
  props: {
    value: {
      type: File,
      default: null
    },
    name: {
      type: String,
      required: true
    },
    label: {
      type: String,
      default: undefined
    },
    description: {
      type: String,
      default: undefined
    },
    placeholder: {
      type: String,
      default: undefined
    },
    fileTypes: {
      type: Array,
      validator: prop => prop?.length > 0,
      default: undefined
    },
    invalid: {
      type: Boolean,
      default: false
    },
    required: {
      type: Boolean,
      default: false
    },
    disabled: {
      type: Boolean,
      default: false
    },
    autofocus: {
      type: Boolean,
      default: false
    }
  },
  data () {
    return {
      isDraggingFile: false,
      inputKey: this.value?.name ?? ''
    }
  },
  computed: {
    hasFile () {
      return !!this.value
    },
    accept () {
      return this.fileTypes?.join(',')
    }
  },
  mounted () {
    if (this.autofocus) {
      this.$nextTick(() => {
        this.$refs.input.focus()
      })
    }
  },
  methods: {
    async receiveFile () {
      const input = this.$refs.input

      if (input.files?.length > 0) {
        const file = input.files[0]
        this.$emit('input', file)
      }
    },
    removeFile () {
      this.$emit('input', null)
      // because we cannot reset the file list of an input[type=file]
      // we must explicitly rerender it, by setting the key to a different value
      this.inputKey = String(Math.ceil(Math.random() * 100000))
      this.$nextTick(() => this.$refs.input.focus())
    }
  }
}
</script>

<style lang="scss" scoped>
@import "@/assets/scss/utilities";

@mixin circle-plus ($color) {
  background-image: url("data:image/svg+xml,%3Csvg xmlns='http://www.w3.org/2000/svg' viewBox='0 0 24 24'%3E%3Cg stroke='#{$color}' fill='none' fill-rule='evenodd'%3E%3Ccircle cx='12' cy='12' r='11.5'/%3E%3Cpath stroke-linecap='round' d='M12 16.9V7.1M7.1 12h9.8'/%3E%3C/g%3E%3C/svg%3E");
  background-repeat: no-repeat;
  background-size: 1.5rem 1.5rem;
  background-position: 0 50%;
}

.f-field__additional-info {
  margin-top: 1rem;
  display: inline-flex;
}

.f-field--file {
  .f-field__input {
    border: 0;
    padding: 0;
    position: relative;
  }

  & > .f-field__label {
    margin-bottom: 1rem;
  }

  & > .f-field__label + .f-field__description {
    margin-top: -1rem;
    margin-bottom: 1rem;
  }

  .f-field__file-input {
    display: block;
    width: 100%;
    position: absolute;
    top: 0;
    left: 0;
    bottom: 0;
    right: 0;
    opacity: 0;
    z-index: 1;
  }

  .f-field__trigger {
    border: 1px dashed $light-blue-700;
    border-radius: 0.375rem;
    display: flex;
    flex-flow: row nowrap;
    align-items: center;
    position: relative;
    min-height: 3.75rem;
    padding: 0.5em 1em;
    line-height: 1.75rem;
    transition-duration: 0.2s;
    transition-timing-function: ease-in;
    transition-property: background-color, border-color;

    .f-field__trigger-text {
      color: $gray-400;
      background-color: transparent;
      border: 0;
      padding-left: 2rem;
      line-height: 1.5rem;
      font-size: 1rem;
      transition: color 0.2s ease-in, background-image 0.2s ease-in;

      @include circle-plus($color: encodecolor($gray-400));
    }
  }

  .f-field__file-input:not(.f-field__file-input--filled):focus,
  .f-field__file-input:not(.f-field__file-input--filled):hover {
    + .f-field__trigger {
      border-color: $blue;

      .f-field__trigger-text {
        color: $blue;
        @include circle-plus($color: encodecolor($blue));
      }
    }
  }

  .f-field__trigger.f-field__trigger--dragging {
    border-color: $light-blue-700;
    background-color: $light-blue;

    .f-field__trigger-text {
      color: $gray-400;
    }
  }
}

.f-field__file-input--filled {
  + .f-field__trigger {
    justify-content: space-between;

    &:hover {
      border-color: initial;
    }

    .f-field__btn-clear {
      background-color: white;
      height: 1rem;
      position: absolute;
      right: 1rem;
      z-index: 1;

      .svg-path {
        fill: $magenta;
        transition: fill 0.2s ease-in;
      }
      &:hover {
        .svg-path {
          fill: $magenta-700;
        }
      }
    }
  }
}

.f-field--danger {
  .f-field__trigger {
    border-color: red;
  }
}
</style>
