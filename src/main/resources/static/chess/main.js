new Vue({
  el: '#app',
  // vuetify: new Vuetify(),
  data: () => ({

    stompClient: null,

    pieceBox: {
      // https://commons.wikimedia.org/wiki/Category:SVG_chess_pieces
      BP: 'https://upload.wikimedia.org/wikipedia/commons/4/49/CommonerB_Transparent.svg',
      BR: 'https://upload.wikimedia.org/wikipedia/commons/f/ff/Chess_rdt45.svg',
      BN: 'https://upload.wikimedia.org/wikipedia/commons/c/c8/Chess_Udt45.svg',
      BB: 'https://upload.wikimedia.org/wikipedia/commons/9/98/Chess_bdt45.svg',
      BQ: 'https://upload.wikimedia.org/wikipedia/commons/4/47/Chess_qdt45.svg',
      BK: 'https://upload.wikimedia.org/wikipedia/commons/f/f0/Chess_kdt45.svg',
      WP: 'https://upload.wikimedia.org/wikipedia/commons/7/7d/Commoner_Transparent.svg',
      WR: 'https://upload.wikimedia.org/wikipedia/commons/7/72/Chess_rlt45.svg',
      WN: 'https://upload.wikimedia.org/wikipedia/commons/8/89/Chess_Ult45.svg',
      WB: 'https://upload.wikimedia.org/wikipedia/commons/b/b1/Chess_blt45.svg',
      WQ: 'https://upload.wikimedia.org/wikipedia/commons/1/15/Chess_qlt45.svg',
      WK: 'https://upload.wikimedia.org/wikipedia/commons/4/42/Chess_klt45.svg'
    },

    board: [
      ['', '', '', '', '', '', '', ''],
      ['', '', '', '', '', '', '', ''],
      ['', '', '', '', '', 'WN', '', ''],
      ['', '', '', '', '', '', '', ''],
      ['', '', '', '', '', '', '', ''],
      ['', '', 'BP', '', '', '', '', ''],
      ['', '', '', '', '', '', '', ''],
      ['', '', '', 'BK', '', '', '', '']
    ],
    whiteOnTop: false,

    dragStartI: -1,
    dragStartJ: -1,

    offsetI: 0,
    offsetJ: 0,
    rows: [""],
    player: '',
    winner: '',
  }),

  methods: {

    connect () {
      let socket = new SockJS('/ttt-subscribe');
      this.stompClient = Stomp.over(socket);
      this.stompClient.connect({}, frame => {
        this.stompClient.subscribe('/ttt/board', this.receiveBoard);
        this.stompClient.subscribe('/ttt/winner', this.receiveWinner);
      });

      // Poor man's callback chain
      let stompOnClose = socket.onclose;
      socket.onclose = status => {
        stompOnClose(status);
        this.stompClient = null;
      }
    },

    receiveBoard (message) {
      let data = JSON.parse(message.body);
      this.offsetI = data[0];
      this.offsetJ = data[1];
      this.rows = data[2];
      this.player = data[3];
    },

    receiveWinner (message) {
      this.winner = message.body;
    },

    restart() {
      this.stompClient.send("/ttt/restart", {}, ".");
      this.winner = '';
    },

    send (i, j) {
      this.stompClient.send("/ttt/click", {}, JSON.stringify([i + this.offsetI, j + this.offsetJ]));
    },

    allowDrop(ev) {
      ev.preventDefault();
    },

    drag(i, j) {
      this.dragStartI = i;
      this.dragStartJ = j;
    },

    drop(ev, i, j) {
      ev.preventDefault();
      this.board[i][j] = this.board[this.dragStartI][this.dragStartJ];
      this.$forceUpdate();
    }
  },

  mounted () {
    this.connect();
  }
});
