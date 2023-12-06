<template>
  <svg
    xmlns="http://www.w3.org/2000/svg"
    viewBox="0 0 56 52"
    width="56px"
    class="help-bubble"
  >
    <g
      fill="none"
      fill-rule="evenodd"
    >
      <path
        vector-effect="non-scaling-stroke"
        stroke="currentColor"
        stroke-width="2"
        d="M33 49.2962318v-8.629075l.8384086-.1372839C46.1540835 38.513263 55 30.3709755 55 21 55 10.0270203 42.963973 1 28 1S1 10.0270203 1 21c0 9.5629844 9.2075597 17.8248072 21.8579308 19.6367309l.2935484.0420452L33 49.2962318z"
      />
      <path
        vector-effect="non-scaling-stroke"
        stroke="currentColor"
        stroke-linecap="round"
        stroke-width="2"
        d="M23 17.59354585c0-3.48291701 2.75146154-5.50882632 5.5-5.59100326 2.75146154-.08226434 5.5 1.8350577 5.5 5.26212072 0 5.55069589-5.9663239 3.11644376-6.11111111 10.06866999"
        class="question-mark"
      />
      <path
        vector-effect="non-scaling-stroke"
        fill="currentColor"
        d="M28 33c-.82772727 0-1.5-.6709091-1.5-1.5S27.17090909 30 28 30s1.5.6709091 1.5 1.5c0 .8277273-.67227273 1.5-1.5 1.5z"
        class="dot"
      />
    </g>
  </svg>
</template>
<style lang="scss">
.animated-icon--is-playing .help-bubble .question-mark {
  animation: hb-question 4s cubic-bezier(0.25, 0.46, 0.45, 0.94) infinite;
  stroke-dasharray: 30px;
}

@keyframes hb-question {
  0%,
  10% {
    stroke-dashoffset: 60px;
  }
  30%,
  40% {
    stroke-dashoffset: 30px;
  }
  70%,
  100% {
    stroke-dashoffset: 0;
  }
}

.animated-icon--is-playing .help-bubble .dot {
  animation: hb-dot 4s cubic-bezier(0.25, 0.46, 0.45, 0.94) infinite;
  transform-origin: 28px 31.5px;
}

@keyframes hb-dot {
  0%,
  20% {
    transform: scale(1);
  }
  40%,
  50% {
    transform: scale(0);
  }
  80%,
  100% {
    transform: scale(1);
  }
}
</style>
