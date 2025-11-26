import 'package:flutter/material.dart';
import 'package:flutter/services.dart';
import 'package:path_provider/path_provider.dart';
import 'package:just_audio/just_audio.dart';
import 'dart:io';

class HomePage extends StatefulWidget {
  const HomePage({super.key});

  @override
  State<HomePage> createState() => _HomePageState();
}

class _HomePageState extends State<HomePage> {
  final AudioPlayer _player = AudioPlayer();
  bool _ready = false;
  double _volume = 1.0;
  double _speed = 1.0;
  Duration? _duration;
  double? _dragValue;
  bool _shuffleEnabled = false;
  LoopMode _loopMode = LoopMode.off;
  int _currentIndex = 0;
  final ScrollController _scroll = ScrollController();
  final List<Map<String, String>> _tracks = [
    {'title': 'Mojito - 周杰伦', 'asset': 'assets/audio/mojito.mp3'},
    {'title': '十年 - 陈奕迅', 'asset': 'assets/audio/十年_陈奕迅.mp3'},
    {'title': '发如雪 - 周杰伦', 'asset': 'assets/audio/发如雪_周杰伦.mp3'},
    {'title': '告白气球', 'asset': 'assets/audio/告白气球.mp3'},
    {'title': '最伟大的作品 - 周杰伦', 'asset': 'assets/audio/最伟大的作品_周杰伦.mp3'},
    {'title': '泡沫 - 邓紫棋', 'asset': 'assets/audio/泡沫_邓紫棋.mp3'},
    {'title': '烟花易冷 - 周杰伦', 'asset': 'assets/audio/烟花易冷_周杰伦.mp3'},
    {'title': '稻香 - 周杰伦', 'asset': 'assets/audio/稻香_周杰伦.mp3'},
    {'title': '诺言 - 郭有才版', 'asset': 'assets/audio/诺言_郭有才版.mp3'},
    {'title': '长安姑娘 - 李常超', 'asset': 'assets/audio/长安姑娘_李常超.mp3'},
  ];

  @override
  void initState() {
    super.initState();
    _init();
    _player.playbackEventStream.listen(
      (_) {},
      onError: (Object e, StackTrace s) async {
        _showToast('播放错误: $e');
        await _skipBadSource();
      },
    );
    _player.currentIndexStream.listen((i) {
      if (i != null) setState(() => _currentIndex = i);
    });
  }

  Future<void> _init() async {
    try {
      final sources = await _resolveSources();
      if (sources.isEmpty) {
        _showToast('未找到可用音频资源');
        return;
      }
      final playlist = ConcatenatingAudioSource(
        children: sources,
        useLazyPreparation: true,
        shuffleOrder: DefaultShuffleOrder(),
      );
      await _player.setAudioSource(
        playlist,
        initialIndex: 0,
        initialPosition: Duration.zero,
      );
      setState(() => _ready = true);
      await _player.load();
      await _player.play();
    } catch (e) {
      _showToast('初始化错误: $e');
    }
  }

  Future<List<AudioSource>> _resolveSources() async {
    final List<AudioSource> list = [];
    final dir = await getTemporaryDirectory();
    for (final t in _tracks) {
      final a = t['asset']!;
      try {
        final data = await rootBundle.load(a);
        final file = File('${dir.path}/${a.split('/').last}');
        await file.writeAsBytes(data.buffer.asUint8List(), flush: true);
        list.add(AudioSource.file(file.path));
      } catch (_) {}
    }
    return list;
  }

  @override
  void dispose() {
    _player.dispose();
    _scroll.dispose();
    super.dispose();
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: const Text('AI 语音新闻'),
        centerTitle: true,
        elevation: 0,
        shape: const RoundedRectangleBorder(
          borderRadius: BorderRadius.vertical(bottom: Radius.circular(16)),
        ),
        flexibleSpace: Container(
          decoration: const BoxDecoration(
            gradient: LinearGradient(
              colors: [Color(0xFF1ED860), Color(0xFF12B34F)],
              begin: Alignment.topLeft,
              end: Alignment.bottomRight,
            ),
          ),
        ),
        actions: [
          IconButton(
            icon: Icon(_shuffleEnabled ? Icons.shuffle_on : Icons.shuffle),
            onPressed: _ready
                ? () async {
                    final v = !_shuffleEnabled;
                    setState(() => _shuffleEnabled = v);
                    await _player.setShuffleModeEnabled(v);
                  }
                : null,
          ),
        ],
      ),
      body: Scrollbar(
        controller: _scroll,
        child: Padding(
          padding: const EdgeInsets.all(16),
          child: SingleChildScrollView(
            controller: _scroll,
            child: Column(
              children: [
                Container(
                  height: 160,
                  width: double.infinity,
                  alignment: Alignment.center,
                  decoration: BoxDecoration(
                    color: Colors.grey.shade200,
                    borderRadius: BorderRadius.circular(8),
                  ),
                  child: const Text('SCI FRI', style: TextStyle(fontSize: 28)),
                ),
                const SizedBox(height: 12),
                Text(
                  _currentTitle,
                  style: const TextStyle(
                    fontSize: 16,
                    fontWeight: FontWeight.w600,
                  ),
                  textAlign: TextAlign.center,
                ),
                const SizedBox(height: 8),
                Row(
                  children: [
                    const Icon(Icons.volume_up),
                    Expanded(
                      child: Slider(
                        min: 0,
                        max: 1,
                        divisions: 10,
                        value: _volume,
                        onChanged: _ready
                            ? (v) {
                                setState(() => _volume = v);
                                _player.setVolume(v);
                              }
                            : null,
                      ),
                    ),
                    PopupMenuButton<double>(
                      initialValue: _speed,
                      onSelected: (v) {
                        setState(() => _speed = v);
                        _player.setSpeed(v);
                      },
                      itemBuilder: (context) => const [
                        PopupMenuItem(value: 0.75, child: Text('0.75x')),
                        PopupMenuItem(value: 1.0, child: Text('1.0x')),
                        PopupMenuItem(value: 1.25, child: Text('1.25x')),
                        PopupMenuItem(value: 1.5, child: Text('1.5x')),
                        PopupMenuItem(value: 2.0, child: Text('2.0x')),
                      ],
                      child: Text('${_speed.toStringAsFixed(2)}x'),
                    ),
                  ],
                ),
                const SizedBox(height: 8),
                Row(
                  mainAxisAlignment: MainAxisAlignment.center,
                  children: [
                    IconButton(
                      icon: const Icon(Icons.skip_previous),
                      onPressed: _ready
                          ? () async {
                              await _player.seekToPrevious();
                              await _player.play();
                            }
                          : null,
                    ),
                    StreamBuilder<PlayerState>(
                      stream: _player.playerStateStream,
                      builder: (context, snapshot) {
                        final playing = snapshot.data?.playing ?? false;
                        return IconButton(
                          icon: Icon(playing ? Icons.pause : Icons.play_arrow),
                          onPressed: _ready
                              ? () => playing ? _player.pause() : _player.play()
                              : null,
                        );
                      },
                    ),
                    IconButton(
                      icon: const Icon(Icons.stop),
                      onPressed: _ready ? () => _player.stop() : null,
                    ),
                    IconButton(
                      icon: const Icon(Icons.skip_next),
                      onPressed: _ready
                          ? () async {
                              await _player.seekToNext();
                              await _player.play();
                            }
                          : null,
                    ),
                  ],
                ),
                const SizedBox(height: 8),
                SingleChildScrollView(
                  scrollDirection: Axis.horizontal,
                  child: Row(
                    children: [
                      ChoiceChip(
                        label: const Text('循环:关'),
                        selected: _loopMode == LoopMode.off,
                        onSelected: _ready
                            ? (_) {
                                setState(() => _loopMode = LoopMode.off);
                                _player.setLoopMode(LoopMode.off);
                              }
                            : null,
                      ),
                      const SizedBox(width: 8),
                      ChoiceChip(
                        label: const Text('循环:列表'),
                        selected: _loopMode == LoopMode.all,
                        onSelected: _ready
                            ? (_) {
                                setState(() => _loopMode = LoopMode.all);
                                _player.setLoopMode(LoopMode.all);
                              }
                            : null,
                      ),
                      const SizedBox(width: 8),
                      ChoiceChip(
                        label: const Text('循环:单曲'),
                        selected: _loopMode == LoopMode.one,
                        onSelected: _ready
                            ? (_) {
                                setState(() => _loopMode = LoopMode.one);
                                _player.setLoopMode(LoopMode.one);
                              }
                            : null,
                      ),
                      const SizedBox(width: 12),
                      FilterChip(
                        label: const Text('随机'),
                        selected: _shuffleEnabled,
                        onSelected: _ready
                            ? (v) {
                                setState(() => _shuffleEnabled = v);
                                _player.setShuffleModeEnabled(v);
                              }
                            : null,
                      ),
                    ],
                  ),
                ),
                const SizedBox(height: 12),
                StreamBuilder<Duration?>(
                  stream: _player.durationStream,
                  builder: (context, snapshot) {
                    _duration = snapshot.data;
                    return StreamBuilder<Duration>(
                      stream: _player.positionStream,
                      builder: (context, posSnap) {
                        final pos = posSnap.data ?? Duration.zero;
                        final max = (_duration ?? Duration.zero).inMilliseconds
                            .toDouble();
                        final value =
                            ((_dragValue ?? pos.inMilliseconds.toDouble())
                                    .clamp(0, max))
                                .toDouble();
                        return Column(
                          children: [
                            Slider(
                              min: 0,
                              max: max == 0 ? 1.0 : max,
                              value: max == 0 ? 0.0 : value,
                              onChanged: _ready
                                  ? (v) => setState(() => _dragValue = v)
                                  : null,
                              onChangeEnd: _ready
                                  ? (v) {
                                      _player.seek(
                                        Duration(milliseconds: v.round()),
                                      );
                                      setState(() => _dragValue = null);
                                    }
                                  : null,
                            ),
                            StreamBuilder<Duration>(
                              stream: _player.bufferedPositionStream,
                              builder: (context, bufSnap) {
                                final b = bufSnap.data ?? Duration.zero;
                                final bt = _formatDuration(b);
                                final ct = _formatDuration(
                                  Duration(milliseconds: value.round()),
                                );
                                final dt = _formatDuration(
                                  _duration ?? Duration.zero,
                                );
                                return Text('$ct / $dt  缓冲 $bt');
                              },
                            ),
                          ],
                        );
                      },
                    );
                  },
                ),
                const SizedBox(height: 12),
                ListView.builder(
                  shrinkWrap: true,
                  physics: const NeverScrollableScrollPhysics(),
                  itemCount: _tracks.length,
                  itemBuilder: (context, i) {
                    final selected = i == _currentIndex;
                    return ListTile(
                      title: Text(_tracks[i]['title']!),
                      selected: selected,
                      onTap: _ready
                          ? () async {
                              await _player.seek(Duration.zero, index: i);
                              await _player.play();
                            }
                          : null,
                    );
                  },
                ),
              ],
            ),
          ),
        ),
      ),
    );
  }

  Future<void> _skipBadSource() async {
    try {
      await _player.seekToNext();
      await _player.play();
    } catch (_) {}
  }

  String get _currentTitle {
    return _tracks[_currentIndex]['title']!;
  }

  String _two(int n) => n.toString().padLeft(2, '0');

  String _formatDuration(Duration d) {
    final m = _two(d.inMinutes.remainder(60));
    final s = _two(d.inSeconds.remainder(60));
    final h = d.inHours;
    return h > 0 ? '$h:$m:$s' : '$m:$s';
  }

  void _showToast(String msg) {
    final snack = SnackBar(
      content: Row(
        children: [
          const Icon(Icons.info_outline, color: Colors.white),
          const SizedBox(width: 8),
          Expanded(
            child: Text(msg, style: const TextStyle(color: Colors.white)),
          ),
        ],
      ),
      backgroundColor: const Color(0xFF1ED860),
      behavior: SnackBarBehavior.floating,
      shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(12)),
      margin: const EdgeInsets.all(12),
      duration: const Duration(seconds: 2),
    );
    ScaffoldMessenger.of(context).showSnackBar(snack);
  }
}
