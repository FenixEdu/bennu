<!--this is an example of a vue component with inputs:
input (--text)
input--email
input--password
input--url
input--phone -->
<template>
    <div class="f-group">
      <div class="f-field">
        <input
          id="fullName"
          v-model="fullName"
          :class="{'f-field__input--is-filled': fullName}"
          name="fullName"
          type="text"
          class="f-field__input"
          required=""
          autocomplete="off"
          autocorrect="off"
          autocapitalize="off"
          spellcheck="false">
        <label
          for="fullName"
          class="f-field__label">Full Name</label>
      </div>
      <div class="f-field f-field--underline">
        <input
          id="fullName"
          v-model="fullName"
          :class="{'f-field__input--is-filled': fullName}"
          name="fullName"
          type="text"
          class="f-field__input"
          required=""
          autocomplete="off"
          autocorrect="off"
          autocapitalize="off"
          spellcheck="false">
        <label
          for="fullName"
          class="f-field__label">Full Name</label>
      </div>
      <div class="f-field f-field--label-inside">
        <input
          id="fullName"
          v-model="fullName"
          :class="{'f-field__input--is-filled': fullName}"
          name="fullName"
          type="text"
          class="f-field__input"
          required=""
          autocomplete="off"
          autocorrect="off"
          autocapitalize="off"
          spellcheck="false">
        <label
          for="fullName"
          class="f-field__label">Full Name</label>
      </div>
    </div>
    <div class="f-group">
      <!-- input type email -->
      <div class="f-field">
        <input
          id="email"
          v-model="email"
          :class="{'f-field__input--is-filled': email}"
          type="email"
          name="email"
          class="f-field__input f-field__input--email"
          required=""
          data-filled="false"
          autocorrect="off"
          autocapitalize="off">
        <label
          for="email"
          class="f-field__label">Email Input</label>
      </div>
      <div class="f-field f-field--underline">
        <input
          id="email"
          v-model="email"
          :class="{'f-field__input--is-filled': email}"
          type="email"
          name="email"
          class="f-field__input f-field__input--email"
          required=""
          data-filled="false"
          autocorrect="off"
          autocapitalize="off">
        <label
          for="email"
          class="f-field__label">Email Input</label>
      </div>
      <div class="f-field f-field--label-inside">
        <input
          id="email"
          v-model="email"
          :class="{'f-field__input--is-filled': email}"
          type="email"
          name="email"
          class="f-field__input f-field__input--email"
          required=""
          data-filled="false"
          autocorrect="off"
          autocapitalize="off">
        <label
          for="email"
          class="f-field__label">Email Input</label>
      </div>
    </div>
    <div class="f-group">
      <!-- input type password -->
      <div class="f-field">
        <input
          id="password"
          v-model="password"
          :class="{'f-field__input--is-filled': password}"
          type="password"
          name="password"
          class="f-field__input f-field__input--password"
          required=""
          data-filled="false"
          autocomplete="off"
          autocorrect="off"
          autocapitalize="off"
          spellcheck="false">
        <label
          for="password"
          class="f-field__label">Password Input</label>
      </div>
      <div class="f-field f-field--underline">
        <input
          id="password"
          v-model="password"
          :class="{'f-field__input--is-filled': password}"
          type="password"
          name="password"
          class="f-field__input f-field__input--password"
          required=""
          data-filled="false"
          autocomplete="off"
          autocorrect="off"
          autocapitalize="off"
          spellcheck="false">
        <label
          for="password"
          class="f-field__label">Password Input</label>
      </div>
      <div class="f-field f-field--label-inside">
        <input
          id="password"
          v-model="password"
          :class="{'f-field__input--is-filled': password}"
          type="password"
          name="password"
          class="f-field__input f-field__input--password"
          required=""
          data-filled="false"
          autocomplete="off"
          autocorrect="off"
          autocapitalize="off"
          spellcheck="false">
        <label
          for="password"
          class="f-field__label">Password Input</label>
      </div>
    </div>
    <div class="f-group">
      <!-- input type url -->
      <div class="f-field">
        <!-- add attribute placeholder="https://" when f-field is focused TODO-->
        <input
          id="url"
          v-model="url"
          :class="{'f-field__input--is-filled': url}"
          type="url"
          name="url"
          class="f-field__input f-field__input--url"
          required=""
          data-filled="false"
          autocorrect="off"
          autocapitalize="off">
        <label
          for="url"
          class="f-field__label">URL Input</label>
      </div>
      <div class="f-field f-field--underline">
        <!-- add attribute placeholder="https://" when f-field is focused TODO-->
        <input
          id="url"
          v-model="url"
          :class="{'f-field__input--is-filled': url}"
          type="url"
          name="url"
          class="f-field__input f-field__input--url"
          required=""
          data-filled="false"
          autocorrect="off"
          autocapitalize="off">
        <label
          for="url"
          class="f-field__label">URL Input</label>
      </div>
      <div class="f-field f-field--label-inside">
        <!-- add attribute placeholder="https://" when f-field is focused TODO-->
        <input
          id="url"
          v-model="url"
          :class="{'f-field__input--is-filled': url}"
          type="url"
          name="url"
          class="f-field__input f-field__input--url"
          required=""
          data-filled="false"
          autocorrect="off"
          autocapitalize="off">
        <label
          for="url"
          class="f-field__label">URL Input</label>
      </div>
    </div>
    <div class="f-group">
      <!-- input type phone -->
      <div class="f-field">
        <input
          id="phone"
          v-model="phone"
          :class="{'f-field__input--is-filled': phone}"
          type="tel"
          name="phone"
          class="f-field__input f-field__input--phone"
          required=""
          data-filled="false"
          autocomplete="off"
          autocorrect="off"
          autocapitalize="off"
          spellcheck="false">
        <div class="f-field__phone-code">
          <span class="f-field__phone-code-flag">
            <svg
              width="21px"
              height="15px"
              viewBox="0 0 21 15"
              version="1.1"
              xmlns="http://www.w3.org/2000/svg"
              xmlns:xlink="http://www.w3.org/1999/xlink">
              <g
                id="Connect---Components"
                stroke="none"
                stroke-width="1"
                fill="none"
                fill-rule="evenodd">
                <g
                  id="1st-Time-Mandatory-2-FA-Config---SMS/Call"
                  transform="translate(-777.000000, -543.000000)"
                  fill-rule="nonzero">
                  <g
                    id="Group-53"
                    transform="translate(737.000000, 240.000000)">
                    <g
                      id="Forms/Inputs/Text/Focus"
                      transform="translate(40.000000, 277.000000)">
                      <g
                        id="Group"
                        transform="translate(0.000000, 26.000000)">
                        <path
                          id="Shape"
                          d="M8,15 L2,15 C0.8954305,15 0,14.1045695 0,13 L0,2 C0,0.8954305 0.8954305,0 2,0 L8,0 L8,15 Z"
                          fill="#24B47E"/>
                        <path
                          id="Shape"
                          d="M19,15 L7,15 L7,0 L19,0 C20.1045695,0 21,0.8954305 21,2 L21,13 C21,14.1045695 20.1045695,15 19,15 Z"
                          fill="#E25950"/>
                        <path
                          id="Shape"
                          d="M10.003,7.5 C10.0027239,5.5499802 8.42176985,3.96936194 6.47175004,3.96950001 C4.52173022,3.96963807 2.94100004,5.55048018 2.94100004,7.5005 C2.94100004,9.45051982 4.52173022,11.0313619 6.47175004,11.0315 C8.42176985,11.0316381 10.0027239,9.4510198 10.003,7.501 L10.003,7.5 Z"
                          fill="#FCD669"/>
                        <path
                          id="Shape"
                          d="M6.425,5.001 C7.80505997,4.95965081 8.95735195,6.0448655 8.99874721,7.42492408 C9.04014248,8.80498267 7.95496626,9.95731088 6.57490906,9.99875222 C5.19485186,10.0401936 4.04248742,8.95505582 4.001,7.575 L4,7.5 C3.99993229,6.14844304 5.07405137,5.04154671 6.425,5.001 Z"
                          fill="#E25950"/>
                        <path
                          id="Shape"
                          d="M5.5,6 L7.5,6 L7.5,8.031 C7.5,8.58328475 7.05228475,9.031 6.5,9.031 C5.94771525,9.031 5.5,8.58328475 5.5,8.031 L5.5,6 Z"
                          fill="#F6F9FC"/>
                      </g>
                    </g>
                  </g>
                </g>
              </g>
            </svg>
          </span>
          <span
            class="f-field__phone-code-number"
            aria-label="Mais 3 5 1"
            lang="pt">+351</span>
          <span class="down-arrow">
            <svg
              width="8px"
              height="5px"
              viewBox="0 0 8 5"
              version="1.1"
              xmlns="http://www.w3.org/2000/svg"
              xmlns:xlink="http://www.w3.org/1999/xlink">
              <g
                id="Connect---Components"
                stroke="none"
                stroke-width="1"
                fill="none"
                fill-rule="evenodd">
                <g
                  id="1st-Time-Mandatory-2-FA-Config---SMS/Call"
                  transform="translate(-848.000000, -548.000000)">
                  <g
                    id="Group-53"
                    transform="translate(737.000000, 240.000000)">
                    <g
                      id="Forms/Inputs/Text/Focus"
                      transform="translate(40.000000, 277.000000)">
                      <g
                        id="Icons/Icon-Down/Blue"
                        transform="translate(66.000000, 24.000000)">
                        <rect
                          id="bounds"
                          fill="#1C172F"
                          opacity="0"
                          x="0"
                          y="0"
                          width="18"
                          height="18"/>
                        <g
                          id="Paths"
                          transform="translate(5.062500, 6.750000)"
                          fill="#8F95A1">
                          <path
                            id="Combined-Shape"
                            d="M3.96492139,3.1667429 L6.60271705,0.528947246 C6.89131819,0.240346102 7.36910116,0.237436867 7.66199438,0.530330086 C7.95692959,0.825265302 7.95550671,1.29747792 7.66337722,1.58960742 L4.62896571,4.62401893 C4.59726454,4.67762581 4.55829357,4.72825012 4.512031,4.77451269 C4.36155389,4.9249898 4.16227506,4.99738975 3.96492139,4.99308359 C3.76756772,4.99738975 3.56828889,4.9249898 3.41781178,4.77451269 C3.3715492,4.72825012 3.33257824,4.67762581 3.30087706,4.62401893 L0.266465557,1.58960742 C-0.0256639408,1.29747792 -0.0270868201,0.825265302 0.267848396,0.530330086 C0.560741615,0.237436867 1.03852458,0.240346102 1.32712573,0.528947246 L3.96492139,3.1667429 Z"/>
                        </g>
                      </g>
                    </g>
                  </g>
                </g>
              </g>
            </svg>
          </span>
        </div>
        <label
          for="phone"
          class="f-field__label">Phone Input</label>
      </div>
      <div class="f-field f-field--underline">
        <input
          id="phone"
          v-model="phone"
          :class="{'f-field__input--is-filled': phone}"
          type="tel"
          name="phone"
          class="f-field__input f-field__input--phone"
          required=""
          data-filled="false"
          autocomplete="off"
          autocorrect="off"
          autocapitalize="off"
          spellcheck="false">
        <div class="f-field__phone-code">
          <span class="f-field__phone-code-flag">
            <svg
              width="21px"
              height="15px"
              viewBox="0 0 21 15"
              version="1.1"
              xmlns="http://www.w3.org/2000/svg"
              xmlns:xlink="http://www.w3.org/1999/xlink">
              <g
                id="Connect---Components"
                stroke="none"
                stroke-width="1"
                fill="none"
                fill-rule="evenodd">
                <g
                  id="1st-Time-Mandatory-2-FA-Config---SMS/Call"
                  transform="translate(-777.000000, -543.000000)"
                  fill-rule="nonzero">
                  <g
                    id="Group-53"
                    transform="translate(737.000000, 240.000000)">
                    <g
                      id="Forms/Inputs/Text/Focus"
                      transform="translate(40.000000, 277.000000)">
                      <g
                        id="Group"
                        transform="translate(0.000000, 26.000000)">
                        <path
                          id="Shape"
                          d="M8,15 L2,15 C0.8954305,15 0,14.1045695 0,13 L0,2 C0,0.8954305 0.8954305,0 2,0 L8,0 L8,15 Z"
                          fill="#24B47E"/>
                        <path
                          id="Shape"
                          d="M19,15 L7,15 L7,0 L19,0 C20.1045695,0 21,0.8954305 21,2 L21,13 C21,14.1045695 20.1045695,15 19,15 Z"
                          fill="#E25950"/>
                        <path
                          id="Shape"
                          d="M10.003,7.5 C10.0027239,5.5499802 8.42176985,3.96936194 6.47175004,3.96950001 C4.52173022,3.96963807 2.94100004,5.55048018 2.94100004,7.5005 C2.94100004,9.45051982 4.52173022,11.0313619 6.47175004,11.0315 C8.42176985,11.0316381 10.0027239,9.4510198 10.003,7.501 L10.003,7.5 Z"
                          fill="#FCD669"/>
                        <path
                          id="Shape"
                          d="M6.425,5.001 C7.80505997,4.95965081 8.95735195,6.0448655 8.99874721,7.42492408 C9.04014248,8.80498267 7.95496626,9.95731088 6.57490906,9.99875222 C5.19485186,10.0401936 4.04248742,8.95505582 4.001,7.575 L4,7.5 C3.99993229,6.14844304 5.07405137,5.04154671 6.425,5.001 Z"
                          fill="#E25950"/>
                        <path
                          id="Shape"
                          d="M5.5,6 L7.5,6 L7.5,8.031 C7.5,8.58328475 7.05228475,9.031 6.5,9.031 C5.94771525,9.031 5.5,8.58328475 5.5,8.031 L5.5,6 Z"
                          fill="#F6F9FC"/>
                      </g>
                    </g>
                  </g>
                </g>
              </g>
            </svg>
          </span>
          <span
            class="f-field__phone-code-number"
            aria-label="Mais 3 5 1"
            lang="pt">+351</span>
          <span class="down-arrow">
            <svg
              width="8px"
              height="5px"
              viewBox="0 0 8 5"
              version="1.1"
              xmlns="http://www.w3.org/2000/svg"
              xmlns:xlink="http://www.w3.org/1999/xlink">
              <g
                id="Connect---Components"
                stroke="none"
                stroke-width="1"
                fill="none"
                fill-rule="evenodd">
                <g
                  id="1st-Time-Mandatory-2-FA-Config---SMS/Call"
                  transform="translate(-848.000000, -548.000000)">
                  <g
                    id="Group-53"
                    transform="translate(737.000000, 240.000000)">
                    <g
                      id="Forms/Inputs/Text/Focus"
                      transform="translate(40.000000, 277.000000)">
                      <g
                        id="Icons/Icon-Down/Blue"
                        transform="translate(66.000000, 24.000000)">
                        <rect
                          id="bounds"
                          fill="#1C172F"
                          opacity="0"
                          x="0"
                          y="0"
                          width="18"
                          height="18"/>
                        <g
                          id="Paths"
                          transform="translate(5.062500, 6.750000)"
                          fill="#8F95A1">
                          <path
                            id="Combined-Shape"
                            d="M3.96492139,3.1667429 L6.60271705,0.528947246 C6.89131819,0.240346102 7.36910116,0.237436867 7.66199438,0.530330086 C7.95692959,0.825265302 7.95550671,1.29747792 7.66337722,1.58960742 L4.62896571,4.62401893 C4.59726454,4.67762581 4.55829357,4.72825012 4.512031,4.77451269 C4.36155389,4.9249898 4.16227506,4.99738975 3.96492139,4.99308359 C3.76756772,4.99738975 3.56828889,4.9249898 3.41781178,4.77451269 C3.3715492,4.72825012 3.33257824,4.67762581 3.30087706,4.62401893 L0.266465557,1.58960742 C-0.0256639408,1.29747792 -0.0270868201,0.825265302 0.267848396,0.530330086 C0.560741615,0.237436867 1.03852458,0.240346102 1.32712573,0.528947246 L3.96492139,3.1667429 Z"/>
                        </g>
                      </g>
                    </g>
                  </g>
                </g>
              </g>
            </svg>
          </span>
        </div>
        <label
          for="phone"
          class="f-field__label">Phone Input</label>
      </div>
      <div class="f-field f-field--label-inside">
        <input
          id="phone"
          v-model="phone"
          :class="{'f-field__input--is-filled': phone}"
          type="tel"
          name="phone"
          class="f-field__input f-field__input--phone"
          required=""
          data-filled="false"
          autocomplete="off"
          autocorrect="off"
          autocapitalize="off"
          spellcheck="false">
        <div class="f-field__phone-code">
          <span class="f-field__phone-code-flag">
            <svg
              width="21px"
              height="15px"
              viewBox="0 0 21 15"
              version="1.1"
              xmlns="http://www.w3.org/2000/svg"
              xmlns:xlink="http://www.w3.org/1999/xlink">
              <g
                id="Connect---Components"
                stroke="none"
                stroke-width="1"
                fill="none"
                fill-rule="evenodd">
                <g
                  id="1st-Time-Mandatory-2-FA-Config---SMS/Call"
                  transform="translate(-777.000000, -543.000000)"
                  fill-rule="nonzero">
                  <g
                    id="Group-53"
                    transform="translate(737.000000, 240.000000)">
                    <g
                      id="Forms/Inputs/Text/Focus"
                      transform="translate(40.000000, 277.000000)">
                      <g
                        id="Group"
                        transform="translate(0.000000, 26.000000)">
                        <path
                          id="Shape"
                          d="M8,15 L2,15 C0.8954305,15 0,14.1045695 0,13 L0,2 C0,0.8954305 0.8954305,0 2,0 L8,0 L8,15 Z"
                          fill="#24B47E"/>
                        <path
                          id="Shape"
                          d="M19,15 L7,15 L7,0 L19,0 C20.1045695,0 21,0.8954305 21,2 L21,13 C21,14.1045695 20.1045695,15 19,15 Z"
                          fill="#E25950"/>
                        <path
                          id="Shape"
                          d="M10.003,7.5 C10.0027239,5.5499802 8.42176985,3.96936194 6.47175004,3.96950001 C4.52173022,3.96963807 2.94100004,5.55048018 2.94100004,7.5005 C2.94100004,9.45051982 4.52173022,11.0313619 6.47175004,11.0315 C8.42176985,11.0316381 10.0027239,9.4510198 10.003,7.501 L10.003,7.5 Z"
                          fill="#FCD669"/>
                        <path
                          id="Shape"
                          d="M6.425,5.001 C7.80505997,4.95965081 8.95735195,6.0448655 8.99874721,7.42492408 C9.04014248,8.80498267 7.95496626,9.95731088 6.57490906,9.99875222 C5.19485186,10.0401936 4.04248742,8.95505582 4.001,7.575 L4,7.5 C3.99993229,6.14844304 5.07405137,5.04154671 6.425,5.001 Z"
                          fill="#E25950"/>
                        <path
                          id="Shape"
                          d="M5.5,6 L7.5,6 L7.5,8.031 C7.5,8.58328475 7.05228475,9.031 6.5,9.031 C5.94771525,9.031 5.5,8.58328475 5.5,8.031 L5.5,6 Z"
                          fill="#F6F9FC"/>
                      </g>
                    </g>
                  </g>
                </g>
              </g>
            </svg>
          </span>
          <span
            class="f-field__phone-code-number"
            aria-label="Mais 3 5 1"
            lang="pt">+351</span>
          <span class="down-arrow">
            <svg
              width="8px"
              height="5px"
              viewBox="0 0 8 5"
              version="1.1"
              xmlns="http://www.w3.org/2000/svg"
              xmlns:xlink="http://www.w3.org/1999/xlink">
              <g
                id="Connect---Components"
                stroke="none"
                stroke-width="1"
                fill="none"
                fill-rule="evenodd">
                <g
                  id="1st-Time-Mandatory-2-FA-Config---SMS/Call"
                  transform="translate(-848.000000, -548.000000)">
                  <g
                    id="Group-53"
                    transform="translate(737.000000, 240.000000)">
                    <g
                      id="Forms/Inputs/Text/Focus"
                      transform="translate(40.000000, 277.000000)">
                      <g
                        id="Icons/Icon-Down/Blue"
                        transform="translate(66.000000, 24.000000)">
                        <rect
                          id="bounds"
                          fill="#1C172F"
                          opacity="0"
                          x="0"
                          y="0"
                          width="18"
                          height="18"/>
                        <g
                          id="Paths"
                          transform="translate(5.062500, 6.750000)"
                          fill="#8F95A1">
                          <path
                            id="Combined-Shape"
                            d="M3.96492139,3.1667429 L6.60271705,0.528947246 C6.89131819,0.240346102 7.36910116,0.237436867 7.66199438,0.530330086 C7.95692959,0.825265302 7.95550671,1.29747792 7.66337722,1.58960742 L4.62896571,4.62401893 C4.59726454,4.67762581 4.55829357,4.72825012 4.512031,4.77451269 C4.36155389,4.9249898 4.16227506,4.99738975 3.96492139,4.99308359 C3.76756772,4.99738975 3.56828889,4.9249898 3.41781178,4.77451269 C3.3715492,4.72825012 3.33257824,4.67762581 3.30087706,4.62401893 L0.266465557,1.58960742 C-0.0256639408,1.29747792 -0.0270868201,0.825265302 0.267848396,0.530330086 C0.560741615,0.237436867 1.03852458,0.240346102 1.32712573,0.528947246 L3.96492139,3.1667429 Z"/>
                        </g>
                      </g>
                    </g>
                  </g>
                </g>
              </g>
            </svg>
          </span>
        </div>
        <label
          for="phone"
          class="f-field__label">Phone Input</label>
      </div>
  </div>
    
</template>

<script>
export default {
  data () {
    return {
      phone: undefined,
      url: undefined,
      email: undefined,
      fullName: undefined
    }
  }
}
</script>
